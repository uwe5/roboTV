package org.xvdr.robotv.client;

import org.xvdr.msgexchange.Packet;

import java.util.ArrayList;

public class Channels extends ArrayList<Channels.Entry> {

    public interface Callback {
        void onChannel(Entry entry);
    }

    public class Entry {
        public long number = 0;
        public String name;
        public int uid = 0;
        public int caid = 0;
        public String iconURL;
        public String serviceReference;
        public boolean radio = false;

    }

    public void load(Connection connection, String language) {
        clear();

        loadChannelType(connection, false, language, null);
    }

    public void load(Connection connection, String language, Callback callback) {
        clear();

        loadChannelType(connection, false, language, callback);
    }

    private boolean loadChannelType(Connection connection, boolean radio, String language, Callback callback) {
        Packet req = connection.CreatePacket(Connection.XVDR_CHANNELS_GETCHANNELS);
        req.putU32(radio ? 1 : 0);
        req.putString(language);
        req.putU32(1);
        req.putU32(0);

        Packet resp = connection.transmitMessage(req);

        if(resp == null) {
            return false;
        }

        while(!resp.eop()) {
            Entry e = new Entry();
            e.number = resp.getU32();
            e.name = resp.getString();
            e.uid = (int) resp.getU32();
            e.caid = (int) resp.getU32();
            e.iconURL = resp.getString();
            e.serviceReference = resp.getString();
            e.radio = radio;

            if(callback != null) {
                callback.onChannel(e);
            }
            else {
                add(e);
            }
        }

        return true;
    }
}
