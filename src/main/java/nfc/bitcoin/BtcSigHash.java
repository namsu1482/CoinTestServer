package nfc.bitcoin;


import org.bouncycastle.jce.provider.BouncyCastleProvider;
import util.Utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.ArrayList;


public class BtcSigHash {
    private static final String TAG = BtcSigHash.class.getSimpleName();

    String DhashPrevOut;
    String DhashSequence;
    String outputPoint;
    String scriptCode;
    String txInAmt;
    String nSequence;
    String DhashOutputs;
    String lockTime;
    String nHashType;
    String inputsData;
    ArrayList<PrevTx> inputs;
    int outputsCnt;
    String outPutsData;

    ArrayList<TxOutPut> outPuts;
    ArrayList<PrevTx> prevTransactionList;
    ArrayList<TxOutPut> outputList;
    String version;

    public BtcSigHash(String version, ArrayList<PrevTx> prevTransactionList, ArrayList<TxOutPut> outputList, String lockTime) {
        this.version = version;
        this.prevTransactionList = prevTransactionList;
        this.outputList = outputList;
        this.lockTime = lockTime;
    }

    //    nVersion:
//    hashPrevouts:
//    hashSequence:
//    outpoint:
//    scriptCode:
//    amount:
//    nSequence:
//    hashOutputs:
//    nLockTime:
//    nHashType:
    public String getBtcSigHash(PrevTx prevTx) {
        StringBuilder sigHashPreImange = new StringBuilder();
        sigHashPreImange.append(version);
        buildHashPrevOut();
        sigHashPreImange.append(DhashPrevOut);
        sigHashPreImange.append(DhashSequence);
        sigHashPreImange.append(prevTx.getOutputPoint());
        sigHashPreImange.append(prevTx.getScriptCode());
        sigHashPreImange.append(prevTx.getAmt());
        sigHashPreImange.append(prevTx.getSequence());

        printData("prevOutAmt", prevTx.getAmt());
        printData("OutputPoint", prevTx.getOutputPoint());
        printData("ScriptCode", prevTx.getScriptCode());
        printData("Sequence", prevTx.getSequence());

        buildOutPuts();
        sigHashPreImange.append(DhashOutputs);
        sigHashPreImange.append(lockTime);
        sigHashPreImange.append("01000000");
        printData("sigPreImage", sigHashPreImange.toString());

        return encrypt(encrypt(sigHashPreImange.toString()));
    }


    private void buildHashPrevOut() {
        StringBuilder prevOutPutBuilder = new StringBuilder();
        StringBuilder hashSequenceBuilder = new StringBuilder();

        for (PrevTx prevTx : prevTransactionList) {
            prevOutPutBuilder.append(prevTx.getOutputPoint());
            hashSequenceBuilder.append(prevTx.getSequence());
        }
        printData("PrevOutPreImage", prevOutPutBuilder.toString());
        printData("SequencesPreImage", hashSequenceBuilder.toString());

        DhashPrevOut = encrypt(encrypt(prevOutPutBuilder.toString()));
        DhashSequence = encrypt(encrypt(hashSequenceBuilder.toString()));

        printData("DhashPrevOut", DhashPrevOut);
        printData("DhashSequence", DhashSequence);

    }

    private void buildOutPuts() {
        StringBuilder stringBuilder = new StringBuilder();
        for (TxOutPut txOutPut : outputList) {
            stringBuilder.append(txOutPut.getAmt() + "16" + txOutPut.getScriptCode());
        }
        printData("outPutsPreImage", stringBuilder.toString());
        DhashOutputs = encrypt(encrypt(stringBuilder.toString()));
        printData("DhashOutputs", DhashOutputs);

    }

    private String encrypt(String value) {
        Security.addProvider(new BouncyCastleProvider());
        String hash = "";


        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hashedString = messageDigest.digest(Utils.hexStringToByteArray(value));
            hash = Utils.byteArrayToHex(hashedString);
//            System.out.println(hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hash;
    }


    private void printData(String dataName, String data) {
        System.out.println(String.format("%s                    :   %s", dataName, data));

    }


}
