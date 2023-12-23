package jmri.jmrit.z21server;

import java.net.InetAddress;
import java.util.Arrays;

public class Service40 {
    private static String moduleIdent = "[Service 40] ";


    public static byte[] handleService(byte[] data, InetAddress clientAddress) {
        int command = data[0];
        switch (command){
            case 0x21:
                return handleHeader21(data[1]);
            case (byte)0xE3:
                return handleHeaderE3(Arrays.copyOfRange(data, 1, 4), clientAddress);
            case (byte)0xE4:
                return handleHeaderE4(Arrays.copyOfRange(data, 1, 5), clientAddress);
            default:
                System.out.println(moduleIdent + "Header " + Integer.toHexString(command) + " not yet supported");
                break;
        }
        return null;
    }

    private static byte[] handleHeader21(int db0){
        switch (db0){
            case 0x21:
                System.out.println(moduleIdent + "Get z21 version");
                break;
            case 0x24:
                System.out.println(moduleIdent + "Get z21 status");
                byte[] answer = new byte[8];
                answer[0] = (byte) 0x08;
                answer[1] = (byte) 0x00;
                answer[2] = (byte) 0x40;
                answer[3] = (byte) 0x00;
                answer[4] = (byte) 0x62;
                answer[5] = (byte) 0x22;
                answer[6] = (byte) 0x00;
                answer[7] = ClientManager.xor(answer);
                return answer;
            case 0x80:
                System.out.println(moduleIdent + "Set track power to off");
                break;
            case 0x81:
                System.out.println(moduleIdent + "Set track power to on");
                break;
            default:
                System.out.println(moduleIdent + "0x21 c pété");
                break;
        }
        return null;
    }

    private static byte[] handleHeaderE3(byte[] data, InetAddress clientAddress) {
        int db0 = data[0];
        if (db0 == (byte)0xF0) {
            // Get loco status command
            int locomotiveAddress = (((data[1] & 0xFF) & 0x3F) << 8) + (data[2] & 0xFF);
            System.out.println(moduleIdent + "Get loco no " + locomotiveAddress + " status");

            ClientManager.getInstance().registerLocoIfNeeded(clientAddress, locomotiveAddress);

            return ClientManager.getInstance().getLocoStatusMessage(clientAddress, locomotiveAddress);

        } else {
            System.out.println(moduleIdent + "Header E3 with function " + Integer.toHexString(db0) + " is not supported");
        }
        return null;
    }

    private static byte[] handleHeaderE4(byte[] data, InetAddress clientAddress) {
        if (data[0] == 0x13) {
            int locomotiveAddress = (((data[1] & 0xFF) & 0x3F) << 8) + (data[2] & 0xFF);
            int rawSpeedData = data[3] & 0xFF;
            boolean bForward = ((rawSpeedData & 0x80) >> 7) == 1;
            int actualSpeed = rawSpeedData & 0x7F;
            System.out.println("Set loco no " + locomotiveAddress + " direction " + (bForward ? "FWD" : "RWD") + " with speed " + actualSpeed);

            ClientManager.getInstance().setLocoSpeedAndDirection(clientAddress, locomotiveAddress, actualSpeed, bForward);

            return ClientManager.getInstance().getLocoStatusMessage(clientAddress, locomotiveAddress);

        }
        return null;
    }
}
