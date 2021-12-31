To release:

0. make_changelog.sh
1. ChangelogGen::main
2. comment gpg entry block in pom.xml
3. release-fxgl.sh
4. Update README.md -> next version
5. Merge dev branch into release

6. revert gpg entry comment

7. Create a GitHub Release, copy CHANGELOG.md, upload uber-jar
8. (Optional) Update all dependencies to their latest versions

Continue developing on the dev branch