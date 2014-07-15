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

    import java.io.BufferedReader;
    import java.io.File;
    import java.io.FileReader;

    /**
     * @author Insunny
     *
     */
public class DelCommentsInJava {

    private static final char MARK = '"';

    private static final char SLASH = '/';

    private static final char BACKSLASH = '\\';

    private static final char STAR = '*';

    private static final char NEWLINE = '\n';

    //引号
    private static final int TYPE_MARK = 1;

    //斜杠
    private static final int TYPE_SLASH = 2;

    //反斜杠
    private static final int TYPE_BACKSLASH = 3;

    //星号
    private static final int TYPE_STAR = 4;

    // 双斜杠类型的注释
    private static final int TYPE_DSLASH = 5;

    /**
     * 删除char[]数组中_start位置到_end位置的元素
     *
     * @param _target
     * @param _start
     * @param _end
     * @return
     */
    public static char[] del(char[] _target, int _start, int _end) {
        char[] tmp = new char[_target.length - (_end - _start + 1)];
        System.arraycopy(_target, 0, tmp, 0, _start);
        System.arraycopy(_target, _end + 1, tmp, _start, _target.length - _end
                - 1);
        return tmp;
    }

    /**
     * 删除代码中的注释
     *
     * @param _target
     * @return
     */
    public static String delComments(String _target) {
        int preType = 0;
        int mark = -1, cur = -1, token = -1;
        // 输入字符串
        char[] input =  _target.toCharArray();
        for (cur = 0; cur < input.length; cur++) {
            String outStr = new String(input);
            String temp = outStr.substring(cur);
            if (input[cur] == MARK) {
                // 首先判断是否为转义引号
                if (preType == TYPE_BACKSLASH)
                    continue;
                // 已经进入引号之内
                if (mark > 0) {
                    // 引号结束
                    mark = -1;
                } else {
                    mark = cur;
                }

                if(preType != TYPE_DSLASH)
                {
                    preType = TYPE_MARK;
                }

            } else if (input[cur] == SLASH) {
                // 当前位置处于引号之中
                if (mark > 0)
                    continue;
                // 如果前一位是*，则进行删除操作
                if (preType == TYPE_STAR) {
                    input = del(input, token, cur);
                    // 退回一个位置进行处理
                    cur = token - 1;
                    preType = 0;
                } else if (preType == TYPE_SLASH)
                {
                    token = cur - 1;
                    preType = TYPE_DSLASH;
                }
                else if (preType == TYPE_DSLASH)
                {
                    continue;
                }
                else
                {
                    preType = TYPE_SLASH;
                }
            }
            else if (input[cur] == BACKSLASH)
            {
                if (preType != TYPE_DSLASH)
                {
                    preType = TYPE_BACKSLASH;
                }
                else
                {
                    continue;
                }

            } else if (input[cur] == STAR) {
                // 当前位置处于引号之中
                if (mark > 0)
                    continue;
                // 如果前一个位置是/,则记录注释开始的位置
                if (preType == TYPE_SLASH) {
                    token = cur - 1;
                }
                preType = TYPE_STAR;
            } else if(input[cur] == NEWLINE)
            {
                if(preType == TYPE_DSLASH)
                {
                    input = del(input, token, cur);
                    // 退回一个位置进行处理
                    cur = token - 1;
                    preType = 0;
                }
            }

        }
        return new String(input);
    }


}

