package ddwu.mobile.finalproject.ma02_20190963;

import android.text.Html;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

public class NaverBlogXmlParser {
    public enum TagType { NONE, TITLE, LINK, DESCRIPTION };

    final static String TAG_ITEM = "item";
    final static String TAG_TITLE = "title";
    final static String TAG_LINK = "link";
    final static String TAG_DESCRIPTION = "description";

    public NaverBlogXmlParser() {    }

    public ArrayList<NaverBlogDto> parse(String xml) {

        ArrayList<NaverBlogDto> resultList = new ArrayList();
        NaverBlogDto dto = null;

        TagType tagType = TagType.NONE;

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xml));

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.END_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (parser.getName().equals(TAG_ITEM)) {
                            dto = new NaverBlogDto();
                        } else if (parser.getName().equals(TAG_TITLE)) {
                            if (dto != null) tagType = TagType.TITLE;
                        } else if (parser.getName().equals(TAG_LINK)) {
                            if (dto != null) tagType = TagType.LINK;
                        } else if (parser.getName().equals(TAG_DESCRIPTION)) {
                            if (dto != null) tagType = TagType.DESCRIPTION;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals(TAG_ITEM)) {
                            resultList.add(dto);
                            dto = null;
                        }
                        break;
                    case XmlPullParser.TEXT:
                        switch(tagType) {
                            case TITLE:
                                dto.setTitle(stripHtml(parser.getText()));
                                break;
                            case LINK:
                                dto.setLink(parser.getText());
                                break;
                            case DESCRIPTION:
                                dto.setDescription(stripHtml(parser.getText()));
                                break;
                        }
                        tagType = TagType.NONE;
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList;
    }

    public String stripHtml(String html) { return Html.fromHtml(html).toString(); }

}
