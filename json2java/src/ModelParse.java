import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
public class ModelParse {

    public static final String NUMBER_TAG = "number";
    public static final String STRING_TAG = "string";
    public static void parseProtocol(JSONObject protocolObject,String keyName,String folderPath,String packageName) throws IOException,JSONException
    {
        keyName = keyName.replaceAll("-","_");
        Iterator<String> keys =  protocolObject.keys();

        if (null != keyName && keyName.length() > 0)
        {
            String parentClass = null;
            String keyArray[] = keyName.split("<");
            if (keyArray.length > 1)
            {
                keyName = keyArray[0];
                parentClass = keyArray[1];
            }

            keyName = keyName.trim();
            File file = new File(folderPath+"/output/"+keyName+".java");
            file.createNewFile();
            FileOutputStream out = new FileOutputStream(file,true);

            StringBuffer sb = new StringBuffer();
            sb.append("\n");
            sb.append("package "+packageName+";");
            sb.append("\n");
            sb.append("import java.util.ArrayList;\n" +
                    "import org.json.JSONArray;\n" +
                    "import org.json.JSONException;\n" +
                    "import org.json.JSONObject;\n" +
                    "import com.external.activeandroid.Model;\n" +
                    "import com.external.activeandroid.DataBaseModel;\n" +
                    "import com.external.activeandroid.annotation.Column;\n" +
                    "import com.external.activeandroid.annotation.Table;\n");
            sb.append("\n");
            sb.append("@Table(name = \""+keyName +"\")");
            sb.append("\n");
            if (null == parentClass)
            {
                sb.append("public class "+ keyName +"  extends DataBaseModel\n{\n");
            }
            else
            {
                sb.append("public class "+ keyName +"   extends "+ parentClass+"\n{\n");
            }

            while (keys.hasNext())
            {
                String key = keys.next();
                Object item =  protocolObject.get(key);

                String column_name = "";
                boolean is_unique = false;

                String key_raw_name = key;

                if (key_raw_name.startsWith("! "))
                {
                    is_unique = true;
                    key_raw_name = key_raw_name.replaceAll("! ", "");
                }

                if (0 == key_raw_name.toLowerCase().compareTo("id"))
                {
                    column_name = keyName+"_id";
                }
                else
                {
                    column_name = key_raw_name;
                }

                if (item.getClass() == JSONObject.class)
                {
                    parseProtocol((JSONObject)item,key, folderPath,packageName);
                }
                else if(item.getClass() == String.class)
                {
                    item = ((String)item).trim();
                     if (((String) item).startsWith("{") && ((String) item).endsWith("}"))
                    {
                        sb.append("\n");
                        sb.append("     @Column(name = \""+column_name +"\")");
                        sb.append("\n");

                        String className = new String((String)item);
                        className = className.trim();
                        className = className.replace("{","");
                        className = className.replace("}","");
                        sb.append("     public "+className +"   "+key_raw_name+";\n");
                    }
                    else if (((String) item).startsWith("<") && ((String) item).endsWith(">"))
                     {
                         sb.append("\n");
                         if (is_unique)
                         {
                             sb.append("     @Column(name = \""+column_name +"\",unique = true)");
                         }
                         else
                         {
                             sb.append("     @Column(name = \""+column_name +"\")");
                         }

                         sb.append("\n");
                         sb.append("     public int "+key_raw_name+";\n");

                     }
                    else
                     {
                         sb.append("\n");
                         sb.append("     @Column(name = \""+column_name +"\")");
                         sb.append("\n");
                         sb.append("     public String" +"   "+key_raw_name+";\n");
                     }
                }
                else if (item.getClass() == Integer.class)
                {

                    sb.append("\n");
                    if (is_unique)
                    {
                        sb.append("     @Column(name = \""+column_name +"\",unique = true)");
                    }
                    else
                    {
                        sb.append("     @Column(name = \""+column_name +"\")");
                    }

                    sb.append("\n");
                    sb.append("     public int "+key_raw_name+";\n");


                }
                else if (item.getClass() == Double.class)
                {
                    sb.append("\n");
                    sb.append("     @Column(name = \""+column_name +"\")");
                    sb.append("\n");
                    sb.append("     public double" +"   "+key_raw_name+";\n");
                }
                else if (item.getClass() == Float.class)
                {
                    sb.append("\n");
                    sb.append("     @Column(name = \""+column_name +"\")");
                    sb.append("\n");
                    sb.append("     public float" +"   "+key_raw_name+";\n");
                }

                else if(item.getClass() == Boolean.class)
                {

                    sb.append("\n");
                    if (is_unique)
                    {
                        sb.append("     @Column(name = \""+column_name +"\",unique = true)");
                    }
                    else
                    {
                        sb.append("     @Column(name = \""+column_name +"\")");
                    }

                    sb.append("\n");
                    sb.append("     public boolean "+key_raw_name+";\n");

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

                        className = className.replace("{", "");
                        className = className.replace("}","");
                        sb.append("\n");
                        sb.append("     public ArrayList<"+className +">   "+key_raw_name+" = new ArrayList<"+className+">();\n");
                    }
                }
            }

            //add fromJson method
            sb = Utils.addfromJson(protocolObject,keyName,sb,parentClass);
            sb = Utils.addtoJson(protocolObject,keyName,sb,parentClass);

            sb.append("\n}\n");

            out.write(sb.toString().getBytes("utf-8"));
            out.close();
        }
        else
        {
            while (keys.hasNext())
            {
                String key = keys.next();
                Object item =  protocolObject.get(key);
                if (item.getClass() == JSONObject.class)
                {
                    parseProtocol((JSONObject)item,key,folderPath,packageName);
                }
            }
        }
    }
}
