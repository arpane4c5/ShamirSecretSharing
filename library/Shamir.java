package library;

import java.math.BigInteger;
import java.util.Random;

public final class Shamir {

    // constructor to initiate the secret sharing. n -> total number of shares ; k -> min required shares
    public Shamir(final int k, final int n) {
        this.k = k;
        this.n = n;

        random = new Random();
    }

    public void setPrime(final BigInteger maxSecret){
        final int modLength = maxSecret.bitLength() + 1;
        prime = new BigInteger(modLength*100, CERTAINTY, random);        
    }

    // splitting the the secret on the basis of SSS algorithm by choosing a random "prime" and coefficients for the equations
    public SecretShare[] split(final BigInteger secret) {
        final BigInteger[] coeff = new BigInteger[k - 1];

        for (int i = 0; i < k - 1; i++) {
            coeff[i] = randomZp(prime);
        }

        final SecretShare[] shares = new SecretShare[n];
        for (int i = 1; i <= n; i++) {
            BigInteger accum = secret;

            for (int j = 1; j < k; j++) {
                final BigInteger t1 = BigInteger.valueOf(i).modPow(BigInteger.valueOf(j), prime);
                final BigInteger t2 = coeff[j - 1].multiply(t1).mod(prime);

                accum = accum.add(t2).mod(prime);
            }
            shares[i - 1] = new SecretShare(i - 1, accum);
        }

        return shares;
    }

    // splitting 1D array of integers into 2D array or secrets
    public SecretShare[][] splitMatrix1D(final BigInteger secret[]){
        SecretShare[][] finalSecrets = new SecretShare[secret.length][];

        for (int i=0; i < secret.length; i++) {
            finalSecrets[i] = split(secret[i]);    
        }

        return finalSecrets;
    }


    // splitting 2D array of integers into 3D array or secrets
    public SecretShare[][][] splitMatrix2D(final BigInteger secret[][]){
        SecretShare[][][] finalSecrets = new SecretShare[secret.length][secret[0].length][];

        for (int i=0; i < secret.length; i++) {
            finalSecrets[i] = splitMatrix1D(secret[i]);    
        }

        return finalSecrets;
    }

    // returns the prime chosen for that SSS instance
    public BigInteger getPrime() {
        return prime;
    }

    // combines k shares to regenerate the secret again
    public BigInteger combine(final SecretShare[] shares, final BigInteger primeNum) {

        BigInteger accum = BigInteger.ZERO;
        for (int i = 0; i < k; i++) {
            BigInteger num = BigInteger.ONE;
            BigInteger den = BigInteger.ONE;

            for (int j = 0; j < k; j++) {
                if (i != j) {
                    num = num.multiply(BigInteger.valueOf(-shares[j].getNum() - 1)).mod(primeNum);
                    den = den.multiply(BigInteger.valueOf(shares[i].getNum() - shares[j].getNum())).mod(primeNum);
                }
            }

            final BigInteger value = shares[i].getShare();

            final BigInteger tmp = value.multiply(num).multiply(den.modInverse(primeNum)).mod(primeNum);
            accum = accum.add(primeNum).add(tmp).mod(primeNum);
        }

        return accum;
    }

    // combines k shares of 2D matrix to regenerate the 1D secret again
    public BigInteger[] combineMatrix1D(final SecretShare[][] shares, final BigInteger primeNum){
        BigInteger[] finalSecret = new BigInteger[shares.length];

        for (int i=0; i < shares.length; i++) {
            finalSecret[i] = combine(shares[i],primeNum);    
        }

        return finalSecret;
    }

    // combines k shares of 3D matrix to regenerate the 2D secret again
    public BigInteger[][] combineMatrix2D(final SecretShare[][][] shares, final BigInteger primeNum){
        BigInteger[][] finalSecret = new BigInteger[shares.length][];

        for (int i=0; i < shares.length; i++) {
            finalSecret[i] = combineMatrix1D(shares[i],primeNum);    
        }

        return finalSecret;
    }

    private BigInteger randomZp(final BigInteger p) {
        while (true) {
            final BigInteger r = new BigInteger(p.bitLength(), random);
            if (r.compareTo(BigInteger.ZERO) > 0 && r.compareTo(p) < 0) {
                return r;
            }
        }
    }

    private BigInteger prime;

    private final int k;
    private final int n;
    private final Random random;

    private static final int CERTAINTY = 100;
}