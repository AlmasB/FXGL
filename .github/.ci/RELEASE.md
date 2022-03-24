To release:

0. comment gpg entry block in pom.xml
1. release-fxgl.sh
2. Update README.md -> next version
3. Merge dev branch into release
4. revert gpg entry comment
5. Create a GitHub Release, copy CHANGELOG.md, upload uber-jar
6. (Optional) Update all dependencies to their latest versions

Continue developing on the dev branch