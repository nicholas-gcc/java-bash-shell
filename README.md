<!-- ABOUT THE PROJECT -->
## Java Bash Shell

This project is a Shell Application that mimics a subset of Bash scripting capabilities in Java. This includes common Unix commands and salient features like redirection, substitution, sequence commands and piping. This project was completed for National University of Singapore's CS4218 Software Testing. 

![image](https://github.com/nicholas-gcc/java-bash-shell/assets/69677864/e685d0b8-be2b-44e0-be13-48b6ea67f540)

<!-- GETTING STARTED -->
## Getting Started

### Prerequisites

Have Java 8 or later installed on your machine.

### Installation

_Below is an example of how you can instruct your audience on installing and setting up your app. This template doesn't rely on any external dependencies or services._

1. Clone the repo
   ```sh
   git clone https://github.com/nicholas-gcc/java-bash-shell.git
   ```
2. Compile the project
   ```sh
   javac -d bin src/main/java/sg/edu/nus/comp/cs4218/*.java src/main/java/sg/edu/nus/comp/cs4218/**/*.java
   ```
   This command compiles all the Java files in the specified directories and saves the compiled bytecode files in the bin directory.


3. Run the entry point of the application
   ```sh
   java -cp bin sg.edu.nus.comp.cs4218.impl.ShellImpl
   ```

## Usage

### Supported Features

1. Piping
   ```sh
   echo "Hello, world!" | tee output.txt
   ```
2. I/O Redirection
   ```sh
   cat file.txt | wc -l > line_count.txt
   ```
3. Sequence Commands
   ```sh
   cp sample.txt new.txt; cut -c 6-7 new.txt
   ```
4. Command Substitution (With single quotation marks)
   ```sh
   echo "'cat file.txt'"
   ```

5. Globbing
   ```sh
   ls somePattern*
   ```

### Commands

| Command      | Flags Supported |
| ----------- | ----------- |
| `cat`      | `-n`       |
| `cd`   | N/A        |
| `cp`   | `-r, -R`        |
| `cut`   | `-b, -c`        |
| `echo`   | N/A        |
| `exit`   | N/A        |
| `grep`   | `-v,-i, -c, -H`        |
| `ls`   | `-R, -X`        |
| `mv`   | `-n`        |
| `paste`   | `-s`        |
| `rm`   | `-r, -d`        |
| `sort`   | `-n, -r, -f`        |
| `tee`   | `a`        |
| `uniq`   | `-c, -d, -D`        |
| `wc`   | `-c, -l, -w`        |




