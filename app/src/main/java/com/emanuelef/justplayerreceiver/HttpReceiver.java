package com.emanuelef.justplayerreceiver;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import fi.iki.elonen.NanoHTTPD;

public class HttpReceiver extends NanoHTTPD {
    private static final String TAG = "HttpReceiver";
    private final Context mContext;

    public HttpReceiver(Context ctx, int port) {
        super(port);
        mContext = ctx;
    }

    @Override
    public Response serve(IHTTPSession session) {
        Log.i(TAG, "new request");

        if ((session.getMethod() != Method.POST) || !session.getUri().endsWith("/play"))
            return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "Not found");

        try {
            String url = session.getParms().get("url");

            if((url == null) || url.isEmpty())
                return newFixedLengthResponse(Response.Status.BAD_REQUEST, MIME_PLAINTEXT, "Missing URL");

            Log.i(TAG, "Request to play " + url);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName("com.brouken.player", "com.brouken.player.PlayerActivity");
            intent.setData(Uri.parse(url));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mContext.startActivity(intent);

            return newFixedLengthResponse("Player started");
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "Internal error: " + e.toString());
        }
    }
}
