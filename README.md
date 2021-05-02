# homework-user-records

## Installation

This project uses Clojure Tools Deps, see [here](https://clojure.org/guides/getting_started) for details.

## Useful Commands

Run Tests: `clj -A:test:test-runner`


Generate Sample Files: `clj -X:gen-samples`
This will cause three sample files to be generated with random user records 
under the `resources` directory with names matching `sample.*`. 
Existing files will be overwritten as applicable.


Run Step 1 `clj -X:step-1`
This will execute the code corresponding to the Step 1 prompt, reading the sample files.

To override the files read, run `clj -X:step-1 :base-filename "resources/example"`
Where `"resources/example"` is the extensionless name of the file(s) you wish to read.

The program will scan for all relevant extensions for the base name (psv, csv, ssv)
