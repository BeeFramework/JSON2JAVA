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
public class ControllerParse
{
    public static void parseProtocol(JSONObject protocolObject,String keyName,String folderPath,String packageName) throws IOException,JSONException
    {
        Iterator<String> keys =  protocolObject.keys();

        if (null != keyName && keyName.length() > 0)
        {
            while (keys.hasNext())
            {
                String key = keys.next();
                JSONObject item =  protocolObject.optJSONObject(key);
                if (key.startsWith("POST"))
                {
                    String interfaceName = key.replaceAll("POST /","");
                    interfaceName = interfaceName.replaceAll("/","");
                     JSONObject requestJson =  item.optJSONObject("request");
                    if (null != requestJson)
                    {
                        ModelParse.parseProtocol(requestJson,interfaceName+"Request",folderPath,packageName);
                    }

                    JSONObject responseJson = item.optJSONObject("response");
                    if (null != requestJson)
                    {
                        ModelParse.parseProtocol(responseJson,interfaceName+"Response",folderPath,packageName);
                    }
                }
            }
        }

        if (null != keyName && keyName.length() > 0)
        {
            keys =  protocolObject.keys();

            File dirFolder = new File(folderPath+"/output");
            if (!dirFolder.exists())
            {
                dirFolder.mkdirs();
            }


            File file = new File(folderPath+"/output/"+"ApiInterface"+".java");

            if (file.exists())
            {
                file.delete();
            }
            file.createNewFile();
            FileOutputStream out = new FileOutputStream(file,true);

            StringBuffer sb = new StringBuffer();
            sb.append("\n");
            sb.append("package "+packageName+";\n");
            sb.append("public class ApiInterface \n{ ");
            sb.append("\n");

            while (keys.hasNext())
            {
                String key = keys.next();
                JSONObject item =  protocolObject.optJSONObject(key);
                if (key.startsWith("POST"))
                {
                    String interfaceContent = key.replaceAll("POST ","");

                    String interfaceTemp = key.replaceAll("POST /","");
                    interfaceTemp = interfaceTemp.replaceAll("-","_");
                    String interfaceName = interfaceTemp.replaceAll("/","_").toUpperCase();



                    sb.append("     public static final String " + interfaceName +"  =" +"\"" + interfaceContent + "\";\n");
                }
            }
            sb.append("\n}");
            out.write(sb.toString().getBytes("utf-8"));
            out.close();

        }
    }

}
