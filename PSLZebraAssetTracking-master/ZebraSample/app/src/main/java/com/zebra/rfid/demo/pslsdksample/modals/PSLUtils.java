package com.zebra.rfid.demo.pslsdksample.modals;

public class PSLUtils {

    public String ConvertHexStringToBinaryString(String strHex)

            throws Exception {

        try {

            String binStr = "";

            int length = strHex.length();

            for (int i = 0; i < length; i++) {

                binStr += CharToBinaryString(strHex.charAt(i));

            }

            return binStr;


        } catch (Exception ex) {


        }

        return null;

    }


    public String CharToBinaryString(char c) throws Exception {

        try {

            switch (c) {

                case '0':

                    return "0000";

                case '1':

                    return "0001";

                case '2':

                    return "0010";

                case '3':

                    return "0011";

                case '4':

                    return "0100";

                case '5':

                    return "0101";

                case '6':

                    return "0110";

                case '7':

                    return "0111";

                case '8':

                    return "1000";

                case '9':

                    return "1001";

                case 'a':

                case 'A':

                    return "1010";

                case 'b':

                case 'B':

                    return "1011";

                case 'c':

                case 'C':

                    return "1100";

                case 'd':

                case 'D':

                    return "1101";

                case 'e':

                case 'E':

                    return "1110";

                case 'f':

                case 'F':

                    return "1111";

                default:

                    throw new Exception("Input is not a  Hex. string");

            }

        } catch (Exception ex) {


        }

        return "";

    }



   /* public TagDetails GetTagInfo(String bstring) {

        TagDetails TD = null;

        try {

            if (bstring.length() != 0) {

                TD = new TagDetails();

                // TD.TruckID=bstring.substring(0, 8), 2)

                // TD.CompanyCode = Integer.toString(Integer.parseInt(

                // bstring.substring(43, 57), 2));

                // TD.Type = Integer.toString(Integer.parseInt(

                // bstring.substring(40, 43), 2));

                // TD.SerialNo = Integer.toString(Integer.parseInt(

                // bstring.substring(57, 96), 2));



                TD.CompanyCode = Integer.toString(Integer.parseInt(

                        bstring.substring(11, 25), 2));

                TD.Type = Integer.toString(Integer.parseInt(

                        bstring.substring(8, 11), 2));

                TD.SerialNo = Long.toString(Long.parseLong(

                        bstring.substring(25, 64), 2));



                return TD;

            } else {

                return null;

            }

        } catch (Exception ex) {


        }

        return TD;

    }*/


    public static String ConvertBinaryStringToAsciiSeven(String binary) throws Exception {

        String asciiseven;

        StringBuilder buffer = new StringBuilder("");

        int len = binary.length();

        for (int i = 0; i < len; i += 7) {

            int j = ToInt32Function(String.valueOf(ConvertBinaryStringToDecimal(PadBinary(binary.substring(i, i+7), 8))));

            buffer.append((char) j);

        }

        asciiseven = buffer.toString();

        return asciiseven;

    }


    public static String PadBinary(String binary, int length) {

        String paddedBinary = "";

        int l = binary.length();

        int pad = (length - (l % length)) % length;

        StringBuilder buffer = new StringBuilder("");

        for (int i = 0; i < pad; i++)

            buffer.append("0");

        buffer.append(binary);

        paddedBinary = buffer.toString();

        return paddedBinary;

    }


    public static long ConvertBinaryStringToDecimal(String strBinary) throws Exception {

        long dec = 0;


        if (strBinary.length() > 64) {

            throw new Exception("String is longer than 64 bits, less than 64 bits is required");

        }


        for (int i = strBinary.length(); i > 0; i--) {

            if (strBinary.charAt(i - 1) != '1' && strBinary.charAt(i-1) != '0')

                throw new Exception("String is not in binary string format");


            long temp = (long) ((strBinary.charAt(i - 1) == '1') ? 1 : 0);


            dec += temp << (strBinary.length() - i);

        }


        return dec;

    }


    public static Integer ToInt32Function(String inputValue) {

        Integer convertedValue = 0;

        try {

            convertedValue = Integer.parseInt(inputValue);

        } catch(Exception e)

        {
        }

        return convertedValue;


    }
}
