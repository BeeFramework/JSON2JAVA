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
public class EnumParse {

    public static void parseProtocol(JSONObject protocolObject,String keyName,String folderPath,String packageName) throws IOException,JSONException
    {
        Iterator<String> keys =  protocolObject.keys();

        if (null != keyName && keyName.length() > 0)
        {
            while (keys.hasNext())
            {
                String key = keys.next();

                Object item =  protocolObject.get(key);

                if (item.getClass() == JSONObject.class)
                {

                    EnumParse.praseEnum((JSONObject)item,key,folderPath,packageName);
                }
            }


        }

    }

    public static void praseEnum(JSONObject protocolObject,String keyName,String folderPath,String packageName) throws IOException,JSONException
    {
        Iterator<String> keys =  protocolObject.keys();

        if (null != keyName && keyName.length() > 0)
        {
            File dirFolder = new File(folderPath+"/output");
            File file = new File(folderPath+"/output/ENUM_"+keyName+".java");

            if (!dirFolder.exists())
            {
                dirFolder.mkdirs();
            }

            if (file.exists())
            {
                file.delete();
            }

            file.createNewFile();
            FileOutputStream out = new FileOutputStream(file,true);

            StringBuffer sb = new StringBuffer();
            sb.append("\n");
            sb.append("package "+packageName+";");
            sb.append("\n");
            sb.append("import java.util.ArrayList;\n" +
                    "import org.json.JSONArray;\n" +
                    "import org.json.JSONException;\n" +
                    "import org.json.JSONObject;\n"
            );
            sb.append("\n");
            sb.append("public enum "+ "ENUM_"+keyName +"\n{\n");

            Class enmuClass = null;

            while (keys.hasNext())
            {

                String key = keys.next();

                Object item =  protocolObject.get(key);


                if(item.getClass() == String.class)
                {
                    sb.append("     "+key+"(\""+item +"\")");
                }
                else if(item.getClass() == Integer.class)
                {
                    sb.append("     "+key+"("+item +")");
                }

                enmuClass = item.getClass();

                if (keys.hasNext())
                {
                    sb.append(",");
                }
                else
                {
                    sb.append(";");
                }

                sb.append("\n");

            }
            
            sb.append("\n     "+"private int value = 0;\n");

            if (enmuClass == String.class)
            {
                sb.append("     "+"private ENUM_"+keyName+"(String initValue)"+"\n     {\n         this.value = initValue;\n     }\n\n");
                sb.append("     "+"public int value()\n     {\n         return this.value;\n     }\n");
            }
            else
            {
                sb.append("     "+"private ENUM_"+keyName+"(int initValue)"+"\n     {\n         this.value = initValue;\n     }\n\n");
                sb.append("     "+"public int value()\n     {\n         return this.value;\n     }\n");
            }


            sb.append("}\n");



            out.write(sb.toString().getBytes("utf-8"));
            out.close();
        }
    }

}
