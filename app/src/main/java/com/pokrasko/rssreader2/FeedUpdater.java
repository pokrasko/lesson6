package com.pokrasko.rssreader2;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by pokrasko on 10.11.14.
 */
public class FeedUpdater extends IntentService {
    private long feedId;
    private String feedTitle;
    private String feedDescription;
    private ResultReceiver receiver;
    private Uri feedUri;

    public static boolean running = false;

    public FeedUpdater() {
        super("FeedUpdater");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String feedUrl;

        running = true;

        feedId = intent.getLongExtra("feed_id", -1);
        feedDescription = intent.getStringExtra("description");
        feedTitle = intent.getStringExtra("title");

        receiver = intent.getParcelableExtra("receiver");

        if (feedId == -1) {
            feedUrl = intent.getStringExtra("url");
            String escapedFeedUrl = DatabaseUtils.sqlEscapeString(feedUrl);
            Cursor cursor = getContentResolver().query(
                    FeedContentProvider.CONTENT_FEEDS_URI,
                    null,
                    "url=" + escapedFeedUrl,
                    null,
                    null
            );
            if (cursor.getCount() != 0) {
                receiver.send(FeedResultReceiver.FEED_EXISTS_ERROR, Bundle.EMPTY);
                return;
            }

            ContentValues values = new ContentValues();
            values.put("title", feedUrl);
            values.put("description", "");
            values.put("url", feedUrl);

            feedTitle = feedUrl;
            feedUri = getContentResolver().insert(FeedContentProvider.CONTENT_FEEDS_URI, values);
            feedId = Long.parseLong(feedUri.getLastPathSegment());
        } else {
            feedUri = Uri.withAppendedPath(FeedContentProvider.CONTENT_FEEDS_URI, "" + feedId);
            Cursor cursor = getContentResolver().query(feedUri, new String[]{"title", "description", "url"}, null, null, null);
            cursor.moveToFirst();
            feedTitle = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            feedDescription = cursor.getString(cursor.getColumnIndexOrThrow("description"));
            feedUrl = cursor.getString(cursor.getColumnIndexOrThrow("url"));
            cursor.close();
        }

        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            XMLReader reader = parser.getXMLReader();
            RSSHandler handler = new RSSHandler();
            reader.setContentHandler(handler);
            InputSource source = new InputSource(new URL(feedUrl).openStream());
            reader.parse(source);


        } catch (ParserConfigurationException e) {
            ReportException(e.toString());
        } catch (SAXException e) {
            ReportException(e.toString());
        } catch (IOException e) {
            ReportException(e.toString());
        }
    }

    @Override
    public void onDestroy() {
        running = true;
        super.onDestroy();
    }

    private void ReportException(String message) {
        Bundle b = new Bundle();
        b.putString("Exception: ", message);
        receiver.send(FeedResultReceiver.EXCEPTION_ERROR, b);
    }

    private class RSSHandler extends DefaultHandler {
        private ContentValues currentValues;
        private boolean saveText;
        private String text = "";

        @Override
        public void startDocument() {
            getContentResolver().delete(FeedContentProvider.CONTENT_POSTS_URI, FeedContentProvider.FEED_ID_FIELD + "=" + feedId, null);
        }

        @Override
        public void startElement(String string, String localName, String qName, Attributes attributes) {
            if (qName.equals("item") || qName.equals("entry")) {
                currentValues = new ContentValues();
            } else if (qName.equals("title") || qName.equals("description") || qName.equals("link")) {
                saveText = true;
                text = "";
            }
            if (currentValues != null && qName.equals("link")) {
                currentValues.put("url", attributes.getValue("href"));
            }
        }

        @Override
        public void endElement(String string, String localName, String qName) {
            saveText = false;
            if (currentValues != null) {
                if (localName.equals("title")) {
                    currentValues.put("title", text.trim());
                } else if (localName.equals("link") && !text.isEmpty()) {
                    currentValues.put("url", text);
                } else if (localName.equals("item") || localName.equals("entry")) {
                    currentValues.put("feed_id", feedId);
                    getContentResolver().insert(FeedContentProvider.CONTENT_POSTS_URI, currentValues);
                    currentValues = null;
                }
            } else if (localName.equals("title")) {
                String newTitle = text.trim();
                if (!newTitle.equals(feedTitle)) {
                    ContentValues values = new ContentValues();
                    values.put("title", newTitle);
                    getContentResolver().update(feedUri, values, null, null);
                }
            } else if (localName.equals("description")) {
                String newDescription = text.trim();
                if (!newDescription.equals(feedDescription)) {
                    ContentValues values = new ContentValues();
                    values.put("description", newDescription);
                    getContentResolver().update(feedUri, values, null, null);
                }
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) {
            String characters = new String(ch, start, length);
            if (saveText) text += characters;
        }
    }
}
