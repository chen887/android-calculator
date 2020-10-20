package com.example.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.View
import kotlinx.android.synthetic.main.main_activity.*
import java.util.*

class MainActivity : AppCompatActivity() {
    var numberStack = Stack<Double>()   //存放数字的栈
    var symbolStack = Stack<Char>() //存放运算符的栈
    var clickOperator = false   //点击运算符按钮是否响应，false为不响应，点击了数字按钮后置为true，即不会出现num++num...

    /**
     * 点击小数点按钮是否响应，初始为true，点击它后置为false，点击运算符后置为true，即一个数字中只有一个小数点
     * 若出现 ".123" 会转为 "0.123"
     * 若出现 "123. op " 则显示不合法
     */
    var clickPoint = true

    /**
     * 是否点击过 =，在点击等号按钮后改为true，点击其他任意按钮后改为false
     * 如果点击 数字按钮 时，clickEqt = true，则清空输入框信息
     * 如果点击 操作符按钮 时，clickEqt = true，则将输出框结果继续用于运算
     */
    var clickEqt = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        eqtBtn.setOnClickListener { //点击等号按钮
            var ss = inputTextView.text.toString()
            if (ss != "") {
                clickEqt = true
                clickPoint = true
                ss += '='

                inputTextView.textSize = 25F
                outputTextView.textSize = 50F


                if (!isStandard(ss)) {
                    outputTextView.text = "表达式不合法"
                } else {
                    outputTextView.text = calculate(ss)

                    //如果文本过长，字体变小
                    val tp = outputTextView.paint
                    tp.textSize = outputTextView.textSize
                    val tvw = tp.measureText(outputTextView.text.toString())
                    if (outputTextView.width < tvw) {
                        outputTextView.textSize = 30F
                    }
                }
            }
        }

        //清空界面，重置计算器
        acBtn.setOnClickListener {
            inputTextView.setText("")
            outputTextView.text = ""
            clickOperator = false
            clickPoint = true
            clickEqt = false
        }

        //删除输入框单个字符
        delBtn.setOnClickListener {
            var ss = inputTextView.text.toString()
            try {
                val lastCh = ss[ss.lastIndex]
                if (ss != "") {
                    if (lastCh == '+' || lastCh == '-' || lastCh == '×' || lastCh == '÷') {
                        clickOperator = true
                    }
                    if (lastCh == '.') {
                        clickPoint = true
                    }
                    ss = ss.substring(0, ss.lastIndex)
                    inputTextView.setText(ss)
                }
            } catch (e: StringIndexOutOfBoundsException) {
                print(e.stackTrace)
            }
        }
    }

    fun clickNumBtn(v: View?) {

        when (v?.id) {
            R.id.bksBtn -> {    //点击括号
                clickNumBtn()
                val ss = inputTextView.text.toString()
                if ((ss == "") || (ss[ss.lastIndex] == '+' || ss[ss.lastIndex] == '-' || ss[ss.lastIndex] == '×' || ss[ss.lastIndex] == '÷' || ss[ss.lastIndex] == '(')) {
                    inputTextView.append("(")
                } else {
                    inputTextView.append(")")
                }

            }
            R.id.modBtn -> {    //点击取模运算
                if (clickOperator) {
                    clickOpBtn()
                    inputTextView.append("%")
                    clickOperator = false
                    clickPoint = true
                }
            }

            R.id.addBtn -> {    //点击加号
                if (clickOperator) {
                    clickOpBtn()
                    inputTextView.append("+")
                    clickOperator = false
                    clickPoint = true
                }
            }
            R.id.redBtn -> {    //点击减号按钮
                if (clickOperator) {
                    clickOpBtn()
                    inputTextView.append("-")
                    clickOperator = false
                    clickPoint = true
                }
            }
            R.id.mtpBtn -> {    //点击乘号按钮
                if (clickOperator) {
                    clickOpBtn()
                    inputTextView.append("×")
                    clickOperator = false
                    clickPoint = true
                }
            }
            R.id.excBtn -> {
                if (clickOperator) {    //点击除号按钮
                    clickOpBtn()
                    inputTextView.append("÷")
                    clickOperator = false
                    clickPoint = true
                }
            }
            R.id.pontBtn -> {   //点击小数点按钮
                if (clickPoint) {
                    inputTextView.append(".")
                    clickPoint = false
                }
            }
            R.id.zeroBtn -> {
                clickNumBtn()
                inputTextView.append("0")
                clickOperator = true
            }
            R.id.oneBtn -> {
                clickNumBtn()
                inputTextView.append("1")
                clickOperator = true
            }
            R.id.twoBtn -> {
                clickNumBtn()
                inputTextView.append("2")
                clickOperator = true
            }
            R.id.threeBtn -> {
                clickNumBtn()
                inputTextView.append("3")
                clickOperator = true
            }
            R.id.fourBtn -> {
                clickNumBtn()
                inputTextView.append("4")
                clickOperator = true
            }
            R.id.fiveBtn -> {
                clickNumBtn()
                inputTextView.append("5")
                clickOperator = true
            }
            R.id.sixBtn -> {
                clickNumBtn()
                inputTextView.append("6")
                clickOperator = true
            }
            R.id.sevenBtn -> {
                clickNumBtn()
                inputTextView.append("7")
                clickOperator = true
            }
            R.id.eightBtn -> {
                clickNumBtn()
                inputTextView.append("8")
                clickOperator = true
            }
            R.id.nineBtn -> {
                clickNumBtn()
                inputTextView.append("9")
                clickOperator = true
            }
        }
    }

    private fun clickOpBtn() {
        outputTextView.textSize = 25F
        inputTextView.textSize = 50F
        if (clickEqt) {
            inputTextView.setText(outputTextView.text)
            clickEqt = false
        }
    }

    private fun clickNumBtn() {
        outputTextView.textSize = 25F
        inputTextView.textSize = 50F
        if (clickEqt) {
            inputTextView.setText("")
            clickEqt = false
        }
    }

    //解析并计算四则运算表达式
    private fun calculate(numStr: String): String {
        symbolStack.push('=')
        var temp = StringBuffer()
        val length = numStr.length - 1
        for (i in 0..length) {
            val ch = numStr[i]
            if (isNumber(ch) || ch == '.' || ch == 'E') {
                temp.append(ch)
            } else {

                try {
                    if (temp[0] == '.') {   //将 .123 转为 0.123
                        temp.insert(0, '0')
                    }
                    if (temp[temp.lastIndex] == '.') {
                        return "表达式不合法"
                    }
                } catch (e: StringIndexOutOfBoundsException) {
                    print(e.stackTrace)
                }
                val tempStr = temp.toString()
                if (tempStr.isNotEmpty()) {
                    val num = tempStr.toDouble()
                    numberStack.push(num)
                    temp = StringBuffer() //重置
                }
                /**
                 * if icp(ch) > isp(top)，令ch进栈
                 * if icp(ch) < isp(top)，top退栈运算
                 * if icp(ch) == isp(top)，top退栈但不操作
                 */
                var top = symbolStack.peek()
                if (icp(ch) > isp(top)) {
                    symbolStack.push(ch)
                } else {
                    while (icp(ch) < isp(top)) {
                        val b = numberStack.pop()
                        val a = numberStack.pop()

                        when (symbolStack.pop()) {
                            '+' -> numberStack.push(a + b)
                            '-' -> numberStack.push(a - b)
                            '×' -> numberStack.push(a * b)
                            '÷' -> {
                                if (b == 0.0) {
                                    return "除数不能为零"
                                }
                                numberStack.push(a / b)
                            }
                            '%' -> numberStack.push(a % b)
                        }
                        top = symbolStack.peek()
                    }
                    if (icp(ch) > isp(top)) {
                        symbolStack.push(ch)
                    }
                    if (icp(ch) == isp(top)) {
                        symbolStack.pop()
                    }
                }

            }
        }
        return numberStack.pop().toString()
    }

    //检查算术表达式的基本合法性，符合返回true，否则false
    private fun isStandard(numStr: String): Boolean {
        //等号前不是数字，如 1+ 或者 ")"
        if (!isNumber(numStr[numStr.lastIndex - 1]) && numStr[numStr.lastIndex - 1] != ')')
            return false

        val stack = Stack<Char>()   // 用来保存括号，检查左右括号是否匹配
        val length = numStr.length - 1
        for (i in 0..length) {
            val n = numStr[i]

            if (!isNumber(n) && n != '+'&& n != '-'&& n != '×'&& n != '÷'&& n != '%'&& n != '('&& n != ')'&& n != '.'&& n != 'E'&& n != '=')
                return false
            // 将左括号压栈，用来给后面的右括号进行匹配
            if (n == '(') {
                stack.push(n)
            }
            if (n == ')') {
                if (stack.isEmpty() || stack.pop() != '(')
                    return false
            }
        }
        // 可能会有缺少右括号的情况
        if (!stack.isEmpty())
            return false
        return true
    }

    //判断字符是否是0-9的数字
    private fun isNumber(num: Char): Boolean {
        if (num in '0'..'9')
            return true
        return false
    }

    //栈内优先数
    private fun isp(ch: Char): Int {
        when (ch) {
            '=' -> return 0
            '(' -> return 1
            '×', '÷', '%' -> return 5
            '+', '-' -> return 3
            ')' -> return 6
        }
        return -1
    }

    //栈外优先数
    private fun icp(ch: Char): Int {
        when (ch) {
            '=' -> return 0
            '(' -> return 6
            '×', '÷', '%' -> return 4
            '+', '-' -> return 2
            ')' -> return 1
        }
        return -1
    }
}


