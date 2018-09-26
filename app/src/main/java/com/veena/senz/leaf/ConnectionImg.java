package com.veena.senz.leaf;


import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


//
//public class MultipartRequest extends Request<String> {
//
//    private MultipartEntity entity = new MultipartEntity();
//
//    private static final String FILE_PART_NAME = "file";
//    private static final String STRING_PART_NAME = "text";
//
//    private final Response.Listener<String> mListener;
//    private final File mFilePart;
//    private final String mStringPart;
//
//    public MultipartRequest(String url, Response.ErrorListener errorListener, Response.Listener<String> listener, File file, String stringPart)
//    {
//        super(Method.POST, url, errorListener);
//
//        mListener = listener;
//        mFilePart = file;
//        mStringPart = stringPart;
//        buildMultipartEntity();
//    }
//
//    private void buildMultipartEntity()
//    {
//        entity.addPart(FILE_PART_NAME, new FileBody(mFilePart));
//        try
//        {
//            entity.addPart(STRING_PART_NAME, new StringBody(mStringPart));
//        }
//        catch (UnsupportedEncodingException e)
//        {
//            VolleyLog.e("UnsupportedEncodingException");
//        }
//    }
//
//    @Override
//    public String getBodyContentType()
//    {
//        return entity.getContentType().getValue();
//    }
//
//    @Override
//    public byte[] getBody() throws AuthFailureError
//    {
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        try
//        {
//            entity.writeTo(bos);
//        }
//        catch (IOException e)
//        {
//            VolleyLog.e("IOException writing to ByteArrayOutputStream");
//        }
//        return bos.toByteArray();
//    }
//
//    @Override
//    protected Response<String> parseNetworkResponse(NetworkResponse response)
//    {
//        return Response.success("Uploaded", getCacheEntry());
//    }
//
//    @Override
//    protected void deliverResponse(String response)
//    {
//        mListener.onResponse(response);
//    }
//}



public class ConnectionImg<T> extends Request<T> {


    private static final String FILE_PART_NAME = "file";

    private MultipartEntityBuilder mBuilder = MultipartEntityBuilder.create();
    private final Response.Listener<T> mListener;
    private final File mImageFile;
    protected Map<String, String> headers;



    public ConnectionImg(String url, ErrorListener errorListener, Listener<T> listener, File imageFile){
        super(Method.POST, url, errorListener);

        mListener = listener;
        mImageFile = imageFile;

        buildMultipartEntity();
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = super.getHeaders();

        if (headers == null
                || headers.equals(Collections.emptyMap())) {
            headers = new HashMap<String, String>();
        }

        headers.put("Accept", "application/json");

        return headers;
    }

    private void buildMultipartEntity(){
        mBuilder.addBinaryBody(FILE_PART_NAME, mImageFile, ContentType.create("image/jpeg"), mImageFile.getName());
        mBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        mBuilder.setLaxMode().setBoundary("xx").setCharset(Charset.forName("UTF-8"));
    }

    @Override
    public String getBodyContentType(){
        String contentTypeHeader = mBuilder.build().getContentType().getValue();
        return contentTypeHeader;
    }

    @Override
    public byte[] getBody() throws AuthFailureError{
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            mBuilder.build().writeTo(bos);
        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream bos, building the multipart request.");
        }

        return bos.toByteArray();
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        T result = null;
        return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }


}

