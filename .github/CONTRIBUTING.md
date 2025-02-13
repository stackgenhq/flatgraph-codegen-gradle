# How to contribute

We are happy to welcome new contributors and make the contribution process as painless as possible. If you have any questions, feel free to open an issue.

Please check the [README](../README.md) to understand the project.

## Getting started

To contribute to the project, please:

1. Fork this repository
2. Create a branch
3. Commit your changes
4. Push your commits to the branch
5. Submit a pull request

Any material change to the released artifact(s) should be accompanied by a `CHANGELOG.md` entry.

For further guidance about getting started, please refer to the related links:

* [Pull Request Guidelines](PULL_REQUEST_TEMPLATE.md)
* Issues Guidelines
    * [Bug Report](ISSUE_TEMPLATE/bug_report.md)
    * [Bug Report](ISSUE_TEMPLATE/feature_request.md)

## Change Tracking

All changes into the `main` branch should come from a Pull Request with a review from one of the [codeowners](CODEOWNERS) and should generally include
an entry in `CHANGELOG.md`'s `[UNRELEASED]` section (do not directly modify a version's changes, this is automatic).

Changelog entries should be organized into appropriate headings per
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and follow the format:

```md
- <contributor>: <Description of change> (#<github issue>)
```

## Coding conventions

In order to sanitize coding standards, please follow the [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html). Any changes should pass ktlint and detekt checks.

## Code of Conduct

This project and everyone participating in it is governed by this [Code of Conduct](CODE_OF_CONDUCT.md).
