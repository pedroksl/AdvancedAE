package net.pedroksl.advanced_ae.recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;

public abstract class IngredientStack<T, P> {
    protected final T ingredient;
    protected int amount;

    IngredientStack(T ingredient, int amount) {
        this.ingredient = ingredient;
        this.amount = amount;
    }

    public static IngredientStack.Item of(ItemStack stack) {
        return new Item(Ingredient.of(stack), stack.getCount());
    }

    public static IngredientStack.Item of(Ingredient ingredient, int amount) {
        return new Item(ingredient, amount);
    }

    public static IngredientStack.Fluid of(FluidStack stack) {
        return new Fluid(stack.getFluid(), stack.getAmount());
    }

    public T getIngredient() {
        return this.ingredient;
    }

    public int getAmount() {
        return this.amount;
    }

    @SuppressWarnings("unchecked")
    public void consume(P stack) {
        if (this.amount <= 0) {
            return;
        }
        if (this.test(stack)) {
            int from = getStackAmount(stack);
            if (from > this.amount) {
                setStackAmount(stack, from - this.amount);
                this.amount = 0;
            } else {
                setStackAmount(stack, 0);
                this.amount -= from;
            }
        }
    }

    // public abstract void consume(P stack);

    public boolean isEmpty() {
        return this.amount <= 0;
    }

    public abstract boolean test(P obj);

    public abstract IngredientStack<T, P> sample();

    public abstract boolean checkType(Object obj);

    public abstract int getStackAmount(P stack);

    public abstract void setStackAmount(P stack, int amount);

    public abstract void toNetwork(FriendlyByteBuf buffer);

    public abstract JsonElement toJson();

    //	public static IngredientStack.Value valueFromJson(JsonObject pJson) {
    //		if (!pJson.has("type")) {
    //			throw new JsonParseException("Fluid Ingredient without type property.");
    //		}
    //
    //		var type = pJson.get("type").toString();
    //		switch (type) {
    //			case "fluid_stack" -> {
    //				return Fluid.fromJson(pJson);
    //			}
    //			case "fluid_tag" -> {
    //				return FluidTagValue.fromJson(pJson);
    //			}
    //			default -> throw new JsonParseException("Fluid Ingredient with an undefined type");
    //		}
    //	}

    public static final class Item extends IngredientStack<Ingredient, ItemStack> {

        public static final Codec<Item> CODEC = RecordCodecBuilder.create(
                builder -> builder.group(ExtraCodecs.JSON.fieldOf("item").forGetter(Item::toJson))
                        .apply(builder, Item::fromJson));

        public Item(Ingredient ingredient, int amount) {
            super(ingredient, amount);
        }

        public boolean test(ItemStack stack) {
            return this.ingredient.test(stack);
        }

        @Override
        public Item sample() {
            return new Item(this.ingredient, this.amount);
        }

        @Override
        public boolean checkType(Object obj) {
            return obj instanceof ItemStack;
        }

        @Override
        public int getStackAmount(ItemStack stack) {
            return stack.getCount();
        }

        @Override
        public void setStackAmount(ItemStack stack, int amount) {
            stack.setCount(amount);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer) {
            this.ingredient.toNetwork(buffer);
            buffer.writeInt(this.amount);
        }

        @Override
        public JsonElement toJson() {
            JsonObject json = new JsonObject();
            json.add("ingredient", this.ingredient.toJson());
            json.addProperty("amount", this.amount);
            return json;
        }

        public static Item fromNetwork(FriendlyByteBuf buffer) {
            var ingredient = Ingredient.fromNetwork(buffer);
            var amount = buffer.readInt();
            return new Item(ingredient, amount);
        }

        public static Item fromJson(@Nullable JsonElement json) {
            if (json != null && !json.isJsonNull()) {
                if (json.isJsonObject()) {
                    var jsonObj = json.getAsJsonObject();
                    var ingredient = Ingredient.fromJson(jsonObj.get("ingredient"), false);
                    var amount = jsonObj.get("amount").getAsInt();
                    return new Item(ingredient, amount);
                } else {
                    throw new JsonSyntaxException("Expected item to be object");
                }
            } else {
                throw new JsonSyntaxException("Item cannot be null");
            }
        }
    }

    public static class Fluid extends IngredientStack<net.minecraft.world.level.material.Fluid, FluidStack> {

        public static final Codec<Fluid> CODEC = RecordCodecBuilder.create(
                builder -> builder.group(FluidStack.CODEC.fieldOf("fluidStack").forGetter(Fluid::getStack))
                        .apply(builder, Fluid::new));

        public Fluid(FluidStack stack) {
            super(stack.getFluid(), stack.getAmount());
        }

        public Fluid(net.minecraft.world.level.material.Fluid fluid, int amount) {
            super(fluid, amount);
        }

        public boolean test(FluidStack stack) {
            return this.ingredient == stack.getFluid();
        }

        @Override
        public Fluid sample() {
            return new Fluid(this.ingredient, this.amount);
        }

        @Override
        public boolean checkType(Object obj) {
            return obj instanceof FluidStack;
        }

        @Override
        public int getStackAmount(FluidStack stack) {
            return stack.getAmount();
        }

        @Override
        public void setStackAmount(FluidStack stack, int amount) {
            stack.setAmount(amount);
        }

        public FluidStack getStack() {
            return new FluidStack(this.ingredient, this.amount);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer) {
            buffer.writeJsonWithCodec(CODEC, this);
        }

        @Override
        public JsonElement toJson() {
            return CODEC.encodeStart(JsonOps.INSTANCE, this).result().get().getAsJsonObject();
        }

        public static Fluid fromNetwork(FriendlyByteBuf buffer) {
            return buffer.readJsonWithCodec(CODEC);
        }

        public static Fluid fromJson(@Nullable JsonElement json) {
            return CODEC.parse(JsonOps.INSTANCE, json).result().get();
        }
    }
}
