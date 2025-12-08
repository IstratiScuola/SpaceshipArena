package common.network;

import java.io.*;

public class InputPacket extends Packet {
    public boolean rotateLeft;
    public boolean rotateRight;
    public boolean throttleUp;
    public boolean throttleDown;
    public boolean throttleMax;
    public boolean throttleZero;
    public boolean shoot;

    public InputPacket() {
        super(PacketType.INPUT);
    }

    public InputPacket(boolean rotateLeft, boolean rotateRight, 
                       boolean throttleUp, boolean throttleDown,
                       boolean throttleMax, boolean throttleZero,
                       boolean shoot) {
        super(PacketType.INPUT);
        this.rotateLeft = rotateLeft;
        this.rotateRight = rotateRight;
        this.throttleUp = throttleUp;
        this.throttleDown = throttleDown;
        this.throttleMax = throttleMax;
        this.throttleZero = throttleZero;
        this.shoot = shoot;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeBoolean(rotateLeft);
        out.writeBoolean(rotateRight);
        out.writeBoolean(throttleUp);
        out.writeBoolean(throttleDown);
        out.writeBoolean(throttleMax);
        out.writeBoolean(throttleZero);
        out.writeBoolean(shoot);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        rotateLeft = in.readBoolean();
        rotateRight = in.readBoolean();
        throttleUp = in.readBoolean();
        throttleDown = in.readBoolean();
        throttleMax = in.readBoolean();
        throttleZero = in.readBoolean();
        shoot = in.readBoolean();
    }
}