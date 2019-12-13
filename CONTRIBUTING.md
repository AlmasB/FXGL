## How to contribute to FXGL

Contribution of any form is welcome!
Please see the list below on how you can contribute to the project.
Once you've decided what you would like to do, let me know about it first.
This is just to make sure that the issue you want hasn't already been implemented,
fixed or being worked on in newer versions.
Any new API or changes to existing API should be discussed to avoid inconsistency.

* Proof read public documentation for errors, ambiguities and typos.
* Crash test features for bugs or write a unit test for one.
* Create an issue or suggest a feature backed up by a use case.
* Provide or suggest an implementation of an issue from [GitHub Issues](https://github.com/AlmasB/FXGL/issues).
* Suggest an API change if you think some calls can be made more concise.
* Add missing tests.
* [Samples](fxgl-samples) might get outdated quite quickly, see if you can find something weird / not working.
* Share details about your project based on FXGL (this will go to the showcase section).
* Write a tutorial on how to use FXGL.

## Building

You need to have JDK 11 to build FXGL.
You can build FXGL from sources using [Maven](https://maven.apache.org/):
```maven
mvn clean package
```

This will create FXGL-x.y.z.jar, sources.jar, javadoc.jar and uber-jar in the "fxgl/target/" directory.

## Workflow

There are 2 branches: `master` and `dev`.
The `master` branch is for releases only.
All the work goes to your `dev` branch, then pull a request back to `dev`.

## Development

Due to use of Kotlin, the only feasible IDE is [IntelliJ IDEA](https://www.jetbrains.com/idea/).
To start developing FXGL you can setup IDEA (2018.3.4+) as follows:

1. Fork FXGL into your GitHub repo, then clone your repo to your machine.
2. `cd` to that directory -> `git checkout dev` to switch to development branch.
3. Open IDEA -> File -> New -> Project from Existing Sources -> Select the cloned FXGL directory.
4. Import project from external model -> Maven -> Next.
5. Tick "Search for projects recursively" and "Import Maven projects automatically" -> Next.
6. Select the provided Maven project to import -> Next -> Use JDK11 -> Next -> Finish.

You should now see something like this:

<img src="https://raw.githubusercontent.com/AlmasB/git-server/master/storage/images/fxgl11_IDEA.jpg" width="400" />

Note: if you have problems running samples / tests with error `RunWithFX`, try right-click on the `fxgl-test` module and rebuild `fxgl-test` module.

## Running all tests

In IntelliJ,

1. Run -> Edit Configurations
2. Add new JUnit configuration with name "ALL FXGL TESTS"
3. Select "Test kind" - "All in package" and "Search for tests" - "In whole project".
4. Apply -> OK.

<img src="https://raw.githubusercontent.com/AlmasB/git-server/master/storage/images/fxgl11_tests.jpg" width="400" />

Now you can run your configuration "ALL FXGL TESTS" which runs tests from all modules.

## Coding Standards

The project uses the following guidelines:

* Tabs set to 4 spaces & consistent indentation.
* Consistent naming conventions (no [Hungarian notation](https://en.wikipedia.org/wiki/Hungarian_notation)).
* Javadoc on public and protected API (where appropriate).
* Keep access to fields and methods as restricted as you can.
* When using text in UI, use localization (e.g. english.properties).
* Short license header in each new file and `@author`.

Code quality reports are available from [Codacy](https://www.codacy.com/app/AlmasB/FXGL/dashboard).
If in doubt, skim through the existing source code to get a feel for it.
