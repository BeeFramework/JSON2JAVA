import java.awt.*;
import java.io.*;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONTokener;
import org.json.JSONObject;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTextArea;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;


public class Main  extends JFrame  {

    public static final String NUMBER_TAG = "number";
    public static final String STRING_TAG = "string";
    private String defaultLocaion;
    public String packetName;

    private static final long serialVersionUID = 1L;
    private JButton openFileBtn;
    private JTextArea showResultTa;
    TextField packetNameTextField;//TextField对象
    Label   packetNamelabel;//TextField对象

    public Main()
    {
        super("JSON2JAVA");
        this.setSize(800, 200);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        packetNameTextField = new TextField("请输入包名，例如:com.insthub.bee.protocol",20);
        packetNameTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                packetName = packetNameTextField.getText();
            }
        });
        packetNamelabel = new Label("包名:");
        openFileBtn = new JButton("打开文件");
        showResultTa = new JTextArea();
        showResultTa.setEnabled(false);
        this.setLayout(new GridLayout(2,2));
        this.add(packetNamelabel);
        this.add(packetNameTextField);
        this.add(openFileBtn);
        this.add(showResultTa);
        openFileBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser fc = new JFileChooser();
                Duqu();
                if (null != defaultLocaion && defaultLocaion.length() > 0)
                {
                    File lastFile = new File(defaultLocaion);
                    File lastDirectory = new File(lastFile.getAbsolutePath());
                    fc.setCurrentDirectory(lastDirectory);
                }

                int retValue = fc.showSaveDialog(Main.this);
                if (retValue == 0)
                {
                    packetName = packetNameTextField.getText();
                    File file = fc.getSelectedFile();
                    defaultLocaion = file.getAbsolutePath();

                    showResultTa.append(file.toString());

                    try
                    {
                        JSONObject protocolObject = Main.getProtocolObject(file.getAbsolutePath());

                        File dirFolder = new File(file.getParent()+"/output");

                        if (!dirFolder.exists())
                        {
                            dirFolder.mkdirs();
                        }

                        JSONObject enumObject = protocolObject.optJSONObject("enum");
                        if (null != enumObject)
                        {
                            EnumParse.parseProtocol(enumObject,"enum",file.getParent(),packetName);
                        }

                        JSONObject modelObject = protocolObject.optJSONObject("model");
                        if (null != modelObject)
                        {
                            ModelParse.parseProtocol(modelObject,"model",file.getParent(),packetName);
                        }

                        JSONObject controllerObject = protocolObject.optJSONObject("controller");
                        if (null != controllerObject)
                        {
                            ControllerParse.parseProtocol(controllerObject,"controller",file.getParent(),packetName);
                        }

                    }
                    catch (IOException exception)
                    {
                        exception.printStackTrace();
                    }
                    catch (JSONException e2)
                    {
                        e2.printStackTrace();
                    }
                    savaLocation(defaultLocaion);
                }
                else
                {
                    //TODO
                }
            }
        });
    }


    public static JSONObject getProtocolObject(String filePath) throws IOException,JSONException
    {
        FileInputStream in = new FileInputStream(filePath);
        int length = in.available();
        byte [] buffer = new byte[length];
        in.read(buffer);
        in.close();
        String res = new String(buffer,"UTF-8");
        res = DelCommentsInJava.delComments(res);
        JSONObject protocolObject = (JSONObject)Main.parseToJSON(res);
        return protocolObject;
    }

    protected static Object parseToJSON(String protocolBody) throws JSONException {
        Object result = null;
        //trim the string to prevent start with blank, and test if the string is valid JSON, because the parser don't do this :(. If Json is not valid this will return null
        protocolBody = protocolBody.trim();
        if(protocolBody.startsWith("{") || protocolBody.startsWith("[")) {
            result = new JSONTokener(protocolBody).nextValue();
        }
        return result;
    }

    public static void main(String[] args)
    {
        new Main().setVisible(true);

    }

    public void savaLocation(String lastFileLocation){//save last location
        File f=new File(".\\json2java\\location.txt");
        try {
            FileWriter txt=new FileWriter(f);
            txt.write(lastFileLocation);
            txt.close();
        } catch (IOException e) {
            // TODO 自动生成 catch 块
            e.printStackTrace();
        }
    }

    //读取保存的用户名和密码
    public void Duqu() {
        FileReader fr;
        try {
            fr = new FileReader(".\\json2java\\location.txt");
            BufferedReader br = new BufferedReader(fr);
            try {
                defaultLocaion = br.readLine();

                //System.out.print(line);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
    }

}
