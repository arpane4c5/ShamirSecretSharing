# ShamirSecretSharing
Java Implementation of SSS algorithm

RUN ANY FILE WITHOUT ARGUMENTS TO GET A LIST OF AVAILABLE OPTIONS 

- The ShamirSecretCreator takes input.txt (default) as argument and divides the specified matrix in that file into N (default = 5) shares and stores them in a folder (default = "shares"

- The ShamirSecretCombine takes the directory of shares as an argument and then asks the user which files does he/she wants to combine to get back the secret.

- The library folder contains the files that contains the actual algorith of SSS.

- SecretShare file is a class to store a single secret data

- Shamir contains method to split a single number/1-D array/2-D array into N shares and to use K secrets to regenerate the secret again.
