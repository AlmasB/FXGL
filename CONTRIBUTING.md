## How to contribute to FXGL

Contribution of any form is welcome! Please see the list below on how you can contribute to the project.
Once you've decided what you would like to do, let me know about it first.
This is just to make sure that the issue you want hasn't already been implemented,
fixed or being worked on in newer versions.

* Proof read public documentation for errors, ambiguities and typos.
* Crash test features for bugs or write a unit test for one.
* Create an issue or suggest a feature backed up by a use case.
* Provide or suggest an implementation of a feature from [New Features](https://github.com/AlmasB/FXGL/wiki/New-Features).
* Fix / implement / close an open issue.
* Suggest an API change if you think some calls can be made more concise.
* [Samples](https://github.com/AlmasB/FXGL/tree/master/samples) might get outdated quite quickly, see if you can find something weird / not working
* Share details about your project based on FXGL (this will go to the showcase section).

## Building

You can build FXGL from sources using [Maven](https://maven.apache.org/):
```maven
mvn package
```

This will generate FXGL-x.y.z.jar, including sources, javadoc and uber-jar in the "target/" directory.

## Workflow

Typically there will be 2 branches: `master` and `x.y.z`.
The `master` branch should always be *clean* and deployable.
Essentially, the `master` branch is for releases.
All the work goes to and stems from the `x.y.z` branch.
Hence, after cloning you should always branch away from `x.y.z` and once
your work is complete, pull a request back to `x.y.z`.

## Coding Standards

The project uses a few guidelines when it comes to code (the list may grow):

* Tabs set to 4 spaces
* Consistent naming conventions (no [Hungarian notation](https://en.wikipedia.org/wiki/Hungarian_notation))
* Javadoc on all public and protected methods

The rest is up to the contributor.
If in doubt, skim through the existing source code to get a feel for it.
