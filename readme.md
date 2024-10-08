![coverage](https://i.ibb.co/MZnwyJW/trext-coverage.png)

![https://i.ibb.co/QDCfKkm/trext-150.png](https://i.ibb.co/QDCfKkm/trext-150.png)

T-Rext is a framework which allows you to automate rest APIs with a rich set of fluent assertions. Its natural assertions and truly helpful error messages improves test code readability and is designed to be super easy for even non-programmers.

The goal is to automate tests without coding everything from the scratch. To know a programming language is NOT required. 

Just a plain editor (notepad, notepad++, sublime, vscode, nano, neovim, etc) and some available http endpoint

## Requirements

- Java >= 8 (Just to run the application, not to write the tests)
- A healthy rest endpoint like `https://api.agify.io/?name=Richard`
- A scenario like: 
    - **Richard** age must be greater than **0**      

## Installer (Linux)

To install or update, you should run the install script. To do that, you may either download and run the script manually, or use the following cURL or Wget command:

```
curl -o- https://raw.githubusercontent.com/jrichardsz-software-architect-tools/t-rext/master/src/main/resources/t-rext.sh | mode=bin bash
```

```
wget -qO- https://raw.githubusercontent.com/jrichardsz-software-architect-tools/t-rext/master/src/main/resources/t-rext.sh | mode=bin bash
```

> Note: Change **master** to pick some specific branch or tag

> Note: For windows users read [this](https://github.com/jrichardsz-software-architect-tools/t-rext/wiki/Installer#for-windows-developerstesters)

In order to validate the installation execute this `t-rext -v`. The result should be

![image](https://github.com/jrichardsz-software-architect-tools/t-rext/assets/3322836/e0c32e81-5293-471b-8b3b-e48687594efa)

## Hello World - Steps

- Create some folder like: `/tmp/hello_world`
- Create a feature inside the recently created folder like: `name_validator.feature`
- Copy and paste the following feature

```
Feature:   Age validation
Ensure that api returns the correct users age

Scenario: Richard's age must greater than 0
url https://api.agify.io/?name=Richard
method get
asserts
assertThat $.age isGreaterThan 0
```

- Execute:

```
t-rext -mode auto -directory /tmp/hello_world
```

Or [this](https://github.com/jrichardsz-software-architect-tools/t-rext/wiki/Execute#for-windows-developers) for windows users


- That's all! You should see an html report in the main folder:

![https://i.ibb.co/4RGbgjx/success-report.png](https://i.ibb.co/4RGbgjx/success-report.png)

More demos [here](https://github.com/jrichardsz-software-architect-tools/t-rext-demo)

## Features

- One feature file could contain several scenarios
- One scenario could use previously saved variables
- Assert validation use [JSON-PATH](https://jsonpath.com/) expressions
- Load global variables with **-variables** parameter
- You could use global variables in any part of your feature files. Example:
  - Rest Api urls
  - Header parameters
  - Query, url and body parameters
- If folder contains several feature files, all of them will be executed. Report changes slightly.
- If exist just one feature, report includes log and metadata in case of error.
- If feature contains several scenarios, you can disabled specific scenario with `disabled true` at scenario level.
- You can comment any line of feature with 	`#`

# Natural Assertions

| assert              | description | example  |
|---------------------|-------------|----------|
| assertThat | mandatory first assert            | assertThat 7 isEqualTo 7 |
| isTrue | looks for a true value  | assertThat $.exist isTrue
| isFalse | looks for a false value | assertThat $.isHero isFalse
| isEqualTo | looks for exact match | assertThat $.content.name isEqualTo "Jane" |
| startsWith | validate if string starts with some value  | assertThat $.content.job startsWith "Dev" |
| endsWith | validate if string value ends with some value  | assertThat "aeiou" endsWith "u" |
| doesNotStartWith | opposite of startsWith | assertThat $.content.job doesNotStartWith "#"  |
| doesNotEndWith | opposite of endsWith | assertThat "aeiou" doesNotEndWith "x" | |
| contains | validate if string contains some value  | assertThat "aeiou" contains "ei" |
| doesNotContain | validate if string does not contain some value  | assertThat "aeiou" doesNotContain "bc" |
| containsOnlyOnce | validate if string contains some value, just one time | assertThat "aeiou" containsOnlyOnce "a" |
| isSubstringOf | validate if string is part of another string | assertThat "ch" isSubstringOf "JRichardsz" |
| doesNotContainAnyWhitespaces | validate if string does not contain any blank space | assertThat $.token doesNotContainAnyWhitespaces |
| isEmpty | validate if string is empty | assertThat $.alias isEmpty |
| isNullOrEmpty | validate if string is null or empty | assertThat $.alias isNullOrEmpty |
| isNotEmpty | opposite of isEmpty | assertThat $.name isNotEmpty |
| isNotNull | similar to isNotEmpty | assertThat $.lastname isNotNull |
| containsOnlyDigits | validate if string contains only numbers | assertThat $.age containsOnlyDigits
| isEqualToIgnoringCase | similar of isEqualTo but without case |  assertThat "PLUSULTRA" isEqualToIgnoringCase "plusultra" |
| isGreaterThan | this > value |  assertThat $.age isGreaterThan 4 |
| isLessThan | this < value |  assertThat $.age isLessThan 10 |
| isGreaterThanOrEqualTo | this >= value |  assertThat $.age isGreaterThanOrEqualTo 5 |
| isLessThanOrEqualTo | this <= value |  assertThat $.age isLessThanOrEqualTo 10 |
| .length() | return the element quantity of collection/array |  assertThat $.books.length() isGreaterThan 5 |

# Special variables

|key|description|
|:--|:--|
|${rand:uuid}| random string|
|${rand:int}| random integer|
|${rand:double}| random double|
|${rand:firstName}| random firstName|
|${rand:lastName}| random lastName|
|${rand:letters}| random letters|

# Advanced settings

More details in [wiki](https://github.com/jrichardsz-software-architect-tools/t-rext/wiki)

# Advanced asserts

https://github.com/jrichardsz-software-architect-tools/t-rext/wiki/Complex-features

# TO DO

- windows installer
- upgrade to java 11
- Apply cobertura maven plugin
- Set coverage minimum threshold of 90
- Add code coverage badges 
- composed variables like ${book_id}-latest in asserts y context
- Add more asserts from: [assertj](https://joel-costigliola.github.io/assertj/core-8/api/org/assertj/core/api/AbstractComparableAssert.html)
- improve the linux script (installer) like nvm
- more dynamic vars like https://learning.postman.com/docs/writing-scripts/script-references/variables-list/ and https://github.com/DiUS/java-faker
- body form url encoded to json
- add log to file
- converter from postman to t-rext
- visual code formatter and color

# Acknowledgments

- [Logo licence](https://creazilla.com/nodes/11735-dinosaur-meal-clipart)
- https://assertj.github.io/doc/


## Contributors

<table>
  <tbody>
    <td>
      <img src="https://avatars0.githubusercontent.com/u/3322836?s=460&v=4" width="100px;"/>
      <br />
      <label><a href="http://jrichardsz.github.io/">JRichardsz</a></label>
      <br />
    </td>    
  </tbody>
</table>
