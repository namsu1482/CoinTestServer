package util;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class Utils {

    public static String dateFormat(String stringDate) {
        String result = "";

        SimpleDateFormat rawDtime = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat formatDtime = new SimpleDateFormat("yyyy.MM.dd  HH:mm:ss");

        try {
            Date date = rawDtime.parse(stringDate);
            result = formatDtime.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static String priceFormat(String priceText) {
        int price = Integer.parseInt(priceText);
        DecimalFormat myFormatter = new DecimalFormat("###,###");
        String formattedStringPrice = myFormatter.format(price);

        return formattedStringPrice;
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }

        return data;
    }

    public static String convertUnixTime(String time) {
        long timestamp = Long.parseLong(time);
        Date date = new Date(timestamp * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+9"));
        String formattedDate = sdf.format(date);

        return formattedDate;
    }

    public static String getDashStyleCardNo(String cardNo) {
        String retString = cardNo;

        if (cardNo != null && cardNo.length() >= 16) {
            retString = String.format("%s-%s",
                    cardNo.substring(0, 6),
                    cardNo.substring(6)
            );
        }

        return retString;
    }

    public static double convertSatoshiToBtc(double value) {
        double btc = value / Math.pow(10, 8);
        BigDecimal btcDecimal = BigDecimal.valueOf(btc);
        return btcDecimal.doubleValue();
    }

    public static double convertEthBalance(double value) {
        double balance = value / Math.pow(10, 18);

        return balance;
    }

    public static double convertEtcGas(String gas, String gasPrice) {
        BigInteger gasBigInteger = new BigInteger(gas);
        BigInteger gasPriceBigInteger = new BigInteger(gasPrice);
        BigDecimal result = BigDecimal.valueOf(gasBigInteger.doubleValue()).multiply(BigDecimal.valueOf(gasPriceBigInteger.doubleValue()));
        result = BigDecimal.valueOf(result.doubleValue() / Math.pow(10, 18));


        return result.doubleValue();
    }

    public static double convertEthBalance(String value) {
        BigInteger bigInteger = new BigInteger(value);
        BigDecimal bigDecimal = BigDecimal.valueOf(bigInteger.doubleValue() / Math.pow(10, 18));


        return bigDecimal.doubleValue();
    }

    public static String convertToWei(double value) {
        BigDecimal pw = BigDecimal.valueOf(Math.pow(10, 18));
        BigDecimal bigDecimal = BigDecimal.valueOf(value).multiply(pw);

        String sendAmt = "0x" + bigDecimal.toBigInteger().toString(16);
        return sendAmt;
    }

    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder();
        for (final byte b : a)
            sb.append(String.format("%02X", b & 0xff));
        return sb.toString();
    }

    public static String hexToAscii(String hexString) {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < hexString.length(); i += 2) {
            String str = hexString.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }
        return output.toString();
    }


    public static String getQueryString(Map<String, Object> map) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean isFirst = true;

        for (Map.Entry<String, Object> param : map.entrySet()) {
            if (isFirst)
                isFirst = false;
            else
                stringBuilder.append("&");

            try {
                stringBuilder.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                stringBuilder.append("=");
                stringBuilder.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return stringBuilder.toString();
    }

    public static byte[] convertToLittleEndian(int value) {
        return new byte[]{
                (byte) value,
                (byte) (value >> 8),
                (byte) (value >> 16),
                (byte) (value >> 24)
        };
    }

    public static String reverseStr(String s) {
        return (new StringBuffer(s)).reverse().toString();
    }

    public static double convertBtcToSatoshi(double value) {
        BigDecimal btcDecimal = BigDecimal.valueOf(value).multiply(BigDecimal.valueOf(Math.pow(10, 8)));
        return btcDecimal.doubleValue();
    }

    public static void reverse(byte[] array) {
        if (array == null) {
            return;
        }
        int i = 0;
        int j = array.length - 1;
        byte tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
    }

    public static String byteArrayToBinaryString(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < b.length; ++i) {
            sb.append(byteToBinaryString(b[i]));
        }
        return sb.toString();
    }

    public static String byteToBinaryString(byte n) {
        StringBuilder sb = new StringBuilder("00000000");
        for (int bit = 0; bit < 8; bit++) {
            if (((n >> bit) & 1) > 0) {
                sb.setCharAt(7 - bit, '1');
            }
        }
        return sb.toString();
    }

    public static String bit5ToBinaryString(byte n) {
        StringBuilder sb = new StringBuilder("00000");
        for (int bit = 0; bit < 5; bit++) {
            if (((n >> bit) & 1) > 0) {
                sb.setCharAt(4 - bit, '1');
            }
        }
        return sb.toString();
    }

    public static byte[] binaryStringToByteArray(String s) {
        int count = s.length() / 8;
        byte[] b = new byte[count];
        for (int i = 1; i < count; ++i) {
            String t = s.substring((i - 1) * 8, i * 8);
            b[i - 1] = binaryStringToByte(t);
        }
        return b;
    }

    public static byte binaryStringToByte(String s) {
        byte ret = 0, total = 0;
        for (int i = 0; i < 8; ++i) {
            ret = (s.charAt(7 - i) == '1') ? (byte) (1 << i) : 0;
            total = (byte) (ret | total);
        }
        return total;
    }




    public static String getEndianValHex(int val) {
        byte[] exchangeBytes = new byte[8];
        byte[] endianExchangeBytes = Utils.convertToLittleEndian(val);
        System.arraycopy(endianExchangeBytes, 0, exchangeBytes, 0, endianExchangeBytes.length);

        String endianExchangeAmt = Utils.byteArrayToHex(exchangeBytes);
        return endianExchangeAmt;
    }

}
