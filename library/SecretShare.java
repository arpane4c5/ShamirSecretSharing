package library;

import java.math.BigInteger;
import java.util.Random;

// class to store the info of single secret
public final class SecretShare {
    public SecretShare(final int num, final BigInteger share) {
        this.num = num;
        this.share = share;
    }

    public int getNum() {
        return num;
    }

    public BigInteger getShare() {
        return share;
    }

    @Override
    public String toString() {
        return "SecretShare [num=" + num + ", share=" + share + "]";
    }

    private final int num;
    private final BigInteger share;
}