package util;

public class Bip {
    public static String[] byte5Arr = {"00000",
            "00001",
            "00010",
            "00011",
            "00100",
            "00101",
            "00110",
            "00111",
            "01000",
            "01001",
            "01010",
            "01011",
            "01100",
            "01101",
            "01110",
            "01111",
            "10000",
            "10001",
            "10010",
            "10011",
            "10100",
            "10101",
            "10110",
            "10111",
            "11000",
            "11001",
            "11010",
            "11011",
            "11100",
            "11101",
            "11110",
            "11111",

    };

    public static String[] valArr = {"q",
            "p",
            "z",
            "r",
            "y",
            "9",
            "x",
            "8",
            "g",
            "f",
            "2",
            "t",
            "v",
            "d",
            "w",
            "0",
            "s",
            "3",
            "j",
            "n",
            "5",
            "4",
            "k",
            "h",
            "c",
            "e",
            "6",
            "m",
            "u",
            "a",
            "7",
            "l"};

    public static String getVal(String byteArr) {
        String val = "";
        for (int i = 0; i < byte5Arr.length; i++) {
            if (byteArr.equals(byte5Arr[i])) {
                val = valArr[i];
                break;
            }

        }
        return val;
    }




}
