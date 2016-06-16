#!/bin/bash
if [ $# = 2 ]; then
  old=$(sed "1q;d" .version)
  before=$(sed "2q;d" .version)
  new="$1"
  next="$2-SNAPSHOT"
  echo "version was: $old"
  echo "releasing version $new"
  sed -i "s/\"$old\"/\"$new\"/g" build.sbt
  sed -i "s/\"$old\"/\"$new\"/g" modules/cobra-server/src/main/scala/net/flatmap/cobra/Cobra.scala
  sed -i "s/$before/$new/g" README.md
  sed -i "s/$old/$new/g" .bintray-deb.yml .bintray-rpm.yml
  git add build.sbt modules/cobra-server/src/main/scala/net/flatmap/cobra/Cobra.scala README.md .bintray-deb.yml .bintray-rpm.yml
  git commit -m "release version $new"
  git tag -a "version-$new" -m "Version $new"
  git push origin "version-$new"
  git push
  echo "setting version to $next"
  sed -i "s/version\\s+:=\\s+\"$old\"/version := \"$new\"/g" build.sbt
  sed -i "s/val\\s+version\\s+=\\s\"$old\"/val version = \"$new\"/g" modules/cobra-server/src/main/scala/net/flatmap/cobra/Cobra.scala
  sed -i "s/$old/$next/g" .version
  sed -i "s/$before/$new/g" .version
else
  echo "usage: ./release.sh <version> <next-version>"
fi