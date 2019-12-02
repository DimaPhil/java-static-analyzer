# java-static-analyzer

Authors:
* Filippov Dmitry, М4138
* Nemchenko Evgeny, М4138

Supported checks:

**Empty `catch`-blocks**

We are finding code blocks, where in `try/catch` constructions the `catch` block is empty and the name of the exception is not `ignored`.

**Unused variables**

We are finding code blocks, where the variable was declared, but is not used:
* is declared, but is not assigned and is not used;
* is declared, is assigned, but is not used;
* is declared, not assigned, but used.

**Incorrect names of constants**

If a variable in the code was declared as `final static`, but not in the upper case, we will show a warning.

**Unused variables in methods**

If some method contains a variable, which is not used in this method, we will show a warning for all such variables.

***How to run***

```bash
java -jar java-static-analyzer.jar <source root> [<output file>]
``` 

The default output file name is `output.txt`.

**Testing**

Folder `samples` contains a bunch of code examples on Java, on which we were testing our tool. The tool is far from ideal and may contain several bugs, because was written in a rush. Also, error handling and logging was not fully finished, we hope that's not crucial :)
