version=$(echo $VERSION | sed -nE 's/([0-9]+)\.([0-9]+)\.([0-9]+)-1.21.1/\1 \2 \3/p')

read -ra v <<< "$version"

case $UPDATE_TYPE in
  breaking)
    ((v[0]++))
    v[1]=0
    v[2]=0
    ;;
  major)
    ((v[1]++))
    v[2]=0
    ;;
  minor)
    ((v[2]++))
    ;;
esac

new_version=${v[0]}.${v[1]}.${v[2]}

echo $new_version

sed -i -E "s/(mod_version=).*(-1.21.1)/\1$new_version\2/" ./gradle.properties
sed -i -E "s/(version_type=).*/\1$RELEASE_TYPE/" ./gradle.properties