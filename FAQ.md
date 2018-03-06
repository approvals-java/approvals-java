# Frequently Asked Questions

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->


- [How to ensure IntelliJ is not creating empty lines in approved files?](#how-to-ensure-intellij-is-not-creating-empty-lines-in-approved-files)
- [How to commit properly approved files in Git?](#how-to-commit-properly-approved-files-in-git)
- [How to use Approvals-Java with another JVM language?](#how-to-use-approvals-java-with-another-jvm-language)
  - [Example solution with ScalaTest](#example-solution-with-scalatest)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## How to ensure IntelliJ is not creating empty lines in approved files? 

**Problem:** When using the default `Windows.IDEA` Reporter, an empty line is added at the end of the *approved* file and the tests never pass.

**Solution:** Add this block to your `.editorconfig` file: 

```
[*.approved]
insert_final_newline = false
```

Or, if you don't use *editorconfig*, check your IDE's formatter settings.

## How to commit properly approved files in Git? 

**Fact:** The `*.approved` files must be checked into source your source control since they are part of your tests. 

**Problem:** You could encounter issues with Git messing with line endings so the file always looks like being modified while running the tests.

**Solution:** Add this block to your `.gitattributes`:

```
*.approved.* binary
*.approved binary
```

Git will then treat the *approved* files as *binary* instead of *text* and will respect their line endings.

## How to use Approvals-Java with another JVM language? 

**Fact:** Approvals tries to name your *approved* files by looking at the stack 
which called `Approvals.verify()` to detect the calling class and method name.

**Problem:** When you use it from a Kotlin or Scala unit test framework, you could have issues with the naming.

### Example solution with ScalaTest

When you use `Approvals.verify()` from a spec, you need to specify the filename for *approved* and *received* files because it is not inferred from the stack like in JUnit tests (the classes and methods are not expressive in this context).

```scala
 "Parser" should "parse example" in {
    val problem = myParser.parse(data)
    approvals.verify(problem, "parsedExample")
  }
```

You can define a trait `Approbation` like this:

```scala
import com.github.writethemfirst.approvals.Approvals

trait Approbation {
  val approvals = new Approvals(getClass)
}
```