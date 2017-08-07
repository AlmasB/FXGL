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
* [Samples](fxgl-samples) might get outdated quite quickly, see if you can find something weird / not working.
* Share details about your project based on FXGL (this will go to the showcase section).

## Building

You need to have Oracle JDK 1.8.0_101+ to build FXGL.
You can build FXGL from sources using [Maven](https://maven.apache.org/):
```maven
mvn clean package
```

This will create FXGL-x.y.z.jar, sources.jar, javadoc.jar and uber-jar in the "fxgl/target/" directory.

## Workflow

Typically there are 2 branches: `master` and `x.y.z`.
The `master` branch is for releases only.
All the work goes to and stems from the `x.y.z` branch.
Hence, after cloning you should always branch away from `x.y.z` and once
your work is complete, pull a request back to `x.y.z`.

## Development

Due to use of Kotlin, the only feasible IDE is [IntelliJ IDEA](https://www.jetbrains.com/idea/).
To start developing FXGL you can setup IDEA (2016.3.3+) as follows:

1. Fork FXGL into your GitHub repo, then clone your repo to your machine.
1. `cd` to that directory -> `git checkout -b BRANCH_NAME x.y.z`, where `x.y.z` is next version and `BRANCH_NAME` is your working branch.
1. Open IDEA -> File -> New -> Project from Existing Sources -> Select the cloned FXGL directory.
1. Import project from external model -> Maven -> Next -> Next.
1. Select the provided Maven project to import -> Next -> Use JDK8+ -> Next -> Finish.

You now should see something like this:

![FXGL](https://raw.githubusercontent.com/AlmasB/git-server/master/storage/images/FXGL_IDEA.jpg)

## Coding Standards

The project uses the following guidelines:

* Tabs set to 4 spaces & consistent indentation.
* Consistent naming conventions (no [Hungarian notation](https://en.wikipedia.org/wiki/Hungarian_notation)).
* Javadoc on public and protected API (where appropriate).
* Keep access to fields and methods as restricted as you can.
* Short license header in each new file and `@author`.

Code quality reports are available from [Codacy](https://www.codacy.com/app/AlmasB/FXGL/dashboard).
If in doubt, skim through the existing source code to get a feel for it.
