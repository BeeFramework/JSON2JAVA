import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/*
 *	 ______    ______    ______
 *	/\  __ \  /\  ___\  /\  ___\
 *	\ \  __<  \ \  __\_ \ \  __\_
 *	 \ \_____\ \ \_____\ \ \_____\
 *	  \/_____/  \/_____/  \/_____/
 *
 *
 *	Copyright (c) 2013-2014, {Bee} open source community
 *	http://www.bee-framework.com
 *
 *
 *	Permission is hereby granted, free of charge, to any person obtaining a
 *	copy of this software and associated documentation files (the "Software"),
 *	to deal in the Software without restriction, including without limitation
 *	the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *	and/or sell copies of the Software, and to permit persons to whom the
 *	Software is furnished to do so, subject to the following conditions:
 *
 *	The above copyright notice and this permission notice shall be included in
 *	all copies or substantial portions of the Software.
 *
 *	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *	FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 *	IN THE SOFTWARE.
 */
public class Utils {

    public static final String NUMBER_TAG = "number";
    public static final String STRING_TAG = "string";

    public static StringBuffer addfromJson(JSONObject protocolObject,String keyName, StringBuffer sb, String parentClass) throws JSONException
    {
        Iterator<String> keys =  protocolObject.keys();
        sb.append("\n");
        sb.append("     public void "  +" fromJson(JSONObject jsonObject)  throws JSONException\n");
        sb.append("     {\n");
        sb.append("          if(null == jsonObject){\n            return ;\n           }\n\n");

        if (null != parentClass)
        {
            sb.append("          super.fromJson(jsonObject)"+";\n\n");
        }

        sb.append("          JSONArray subItemArray;\n");
        while (keys.hasNext())
        {
            String key = keys.next();
            Object item =  protocolObject.get(key);
            String key_raw_name = key;

            if (key_raw_name.startsWith("! "))
            {
                key_raw_name = key_raw_name.replaceAll("! ", "");
            }

            if (item.getClass() == JSONObject.class)
            {

            }
            else if(item.getClass() == String.class)
            {
                item = ((String)item).trim();

                if (((String) item).startsWith("{") && ((String) item).endsWith("}"))
                {
                    String className = new String((String)item);
                    className = className.trim();
                    className = className.replace("{","");
                    className = className.replace("}","");
                    sb.append("          "+className +"  "+key_raw_name+" = new "+className+"();\n");
                    sb.append("          "+key_raw_name+".fromJson(jsonObject.optJSONObject(\""+key_raw_name+"\"));\n");
                    sb.append("          this."+key_raw_name+" = "+key_raw_name + ";\n");
                }
                else if (((String) item).startsWith("<") && ((String) item).endsWith(">"))
                {
                    sb.append("\n");
                    sb.append("          this."+key_raw_name+" = "+"jsonObject.optInt(\""+key_raw_name+"\");\n" );
                }
                else
                {
                    sb.append("\n");
                    sb.append("          this."+key_raw_name+" = "+"jsonObject.optString(\""+key_raw_name+"\");\n" );
                }

            }
            else if (item.getClass() == Integer.class)
            {
                sb.append("\n");
                sb.append("          this."+key_raw_name+" = "+"jsonObject.optInt(\""+key_raw_name+"\");\n" );
            }
            else if (item.getClass() == Boolean.class)
            {
                sb.append("\n");
                sb.append("          this."+key_raw_name+" = "+"jsonObject.optBoolean(\""+key_raw_name+"\");\n" );
            }
            else if (item.getClass() == Double.class)
            {
                sb.append("\n");
                sb.append("          this."+key_raw_name+" = "+"jsonObject.optDouble(\""+key_raw_name+"\");\n" );
            }
            else if (item.getClass() == Float.class)
            {
                sb.append("\n");
                sb.append("          this."+key_raw_name+" = "+"jsonObject.optDouble(\""+key_raw_name+"\");\n" );
            }
            else if (item.getClass() == JSONArray.class)
            {
                JSONArray itemJSONArray = (JSONArray)item;
                if (itemJSONArray.length() > 0)
                {
                    Object subItem =  itemJSONArray.opt(0);
                    String className = "";
                    if (subItem.getClass() == String.class)
                    {
                        if (((String)subItem).startsWith("{"))
                        {
                            className = new String((String)subItem);
                        }
                        else
                        {
                            className = "String";
                        }

                    }
                    else if (subItem.getClass() == Integer.class)
                    {
                        className = "Integer";
                    }


                    className = className.replace("[","");
                    className = className.replace("]","");
                    className = className.trim();

                    className = className.replace("{","");
                    className = className.replace("}","");
                    sb.append("\n");
                    sb.append("          subItemArray"+" = "+"jsonObject.optJSONArray(\""+key_raw_name+"\");\n" );

                    sb.append("          if(null != subItemArray)\n           {\n");
                    sb.append("              for(int i = 0;i < subItemArray.length();i++)\n               {\n");


                    if (0 == className.compareTo("String"))
                    {
                        sb.append("                  String subItemObject = subItemArray.optString(i);\n");
                        sb.append("                  "+className +" subItem = subItemObject;\n");
                    }
                    else if (0 == className.compareTo("Integer"))
                    {
                        sb.append("                  int subItemObject = subItemArray.optInt(i);\n");
                        sb.append("                  "+className +" subItem = Integer.valueOf(subItemObject);\n");
                    }
                    else
                    {
                        sb.append("                  JSONObject subItemObject = subItemArray.getJSONObject(i);\n");
                        sb.append("                  "+className +" subItem = new "+className+"();\n");
                        sb.append("                  "+"subItem.fromJson(subItemObject);\n");
                    }

                    sb.append("                  this."+key_raw_name+".add(subItem);\n");
                    sb.append("               }\n");
                    sb.append("           }\n\n");

                }
            }


        }

        sb.append("          return ;\n     }\n");
        return sb;
    }

    public static StringBuffer addtoJson(JSONObject protocolObject,String keyName, StringBuffer sb,String parentClass) throws JSONException
    {
        Iterator<String> keys =  protocolObject.keys();
        sb.append("\n");
        sb.append("     public JSONObject " +" toJson() throws JSONException \n");
        sb.append("     {\n");
        sb.append("          JSONObject localItemObject = new JSONObject();\n");
        if (null != parentClass)
        {
            sb.append("          localItemObject" + " = " +"super.toJson()"+";\n\n");
        }
        sb.append("          JSONArray itemJSONArray = new JSONArray();\n");

        while (keys.hasNext())
        {
            String key = keys.next();
            Object item =  protocolObject.get(key);

            String key_raw_name = key;

            if (key_raw_name.startsWith("! "))
            {
                key_raw_name = key_raw_name.replaceAll("! ", "");
            }

            if(item.getClass() == String.class)
            {
                item = ((String)item).trim();

                if (((String) item).startsWith("{") && ((String) item).endsWith("}"))
                {
                    String className = new String((String)item);
                    className = className.trim();
                    className = className.replace("{","");
                    className = className.replace("}","");
                    sb.append("          if(null != "+key_raw_name +")\n          {\n            localItemObject.put(\""+key_raw_name+"\", "+key_raw_name +".toJson());\n          }\n");
                }
                else if(((String) item).startsWith("<") && ((String) item).endsWith(">"))
                {
                    sb.append("          localItemObject.put(\""+key_raw_name+"\", "+key_raw_name +");\n");
                }
                else
                {
                    sb.append("          localItemObject.put(\""+key_raw_name+"\", "+key_raw_name +");\n");
                }
            }
            else if (item.getClass() == Integer.class)
            {
                sb.append("          localItemObject.put(\""+key_raw_name+"\", "+key_raw_name +");\n");
            }
            else if (item.getClass() == Boolean.class)
            {
                sb.append("          localItemObject.put(\""+key_raw_name+"\", "+key_raw_name +");\n");
            }
            else if (item.getClass() == Double.class)
            {
                sb.append("          localItemObject.put(\""+key_raw_name+"\", "+key_raw_name +");\n");
            }
            else if (item.getClass() == Float.class)
            {
                sb.append("          localItemObject.put(\""+key_raw_name+"\", "+key_raw_name +");\n");
            }
            else if (item.getClass() == JSONArray.class)
            {
                JSONArray itemJSONArray = (JSONArray)item;
                if (itemJSONArray.length() > 0)
                {
                    Object subItem =  itemJSONArray.opt(0);
                    String className = "";
                    if (subItem.getClass() == String.class)
                    {
                        if (((String)subItem).startsWith("{"))
                        {
                            className = new String((String)subItem);
                        }
                        else
                        {
                            className = "String";
                        }

                    }
                    else if (subItem.getClass() == Integer.class)
                    {
                        className = "Integer";
                    }

                    className = className.replace("[","");
                    className = className.replace("]","");
                    className = className.trim();

                    className = className.replace("{","");
                    className = className.replace("}","");
                    sb.append("\n");
                    sb.append("          for(int i =0; i< "+key_raw_name+".size(); i++)\n");
                    sb.append("          {\n");

                    if (0 == className.compareTo("String"))
                    {
                        sb.append("              " + className +" itemData ="+key_raw_name+".get(i);\n");
                        sb.append("              itemJSONArray.put(itemData);\n");
                    }
                    else if (0 == className.compareTo("Integer"))
                    {
                        sb.append("              " + className +" itemData ="+key_raw_name+".get(i);\n");
                        sb.append("              itemJSONArray.put(itemData.intValue());\n");
                    }
                    else
                    {
                        sb.append("              " + className +" itemData ="+key_raw_name+".get(i);\n");
                        sb.append("              JSONObject itemJSONObject = itemData.toJson();\n");
                        sb.append("              itemJSONArray.put(itemJSONObject);\n");
                    }

                    sb.append("          }\n");
                    sb.append("          localItemObject.put(\""+key_raw_name +"\", itemJSONArray);\n");

                }
            }
        }

        sb.append("          return localItemObject;\n     }\n");
        return sb;
    }

}
