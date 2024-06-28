package com.example.calculadora13

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import net.objecthunter.exp4j.ExpressionBuilder
import kotlin.math.PI
import kotlin.math.sqrt

class MainActivity : AppCompatActivity() {

    private lateinit var tvResult: TextView
    private lateinit var tvTemp: TextView
    private var currentExpression: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvResult = findViewById(R.id.tvResult)
        tvTemp = findViewById(R.id.tvTemp)
        tvTemp.text = "0"
        tvResult.text = "0"
    }

    fun onClickNumber(view: View) {
        if (view is Button) {
            val buttonText = view.text.toString()

            if (buttonText == ".") {
                // Verificar si el último número ya contiene un punto decimal
                val lastNumber = currentExpression.split(Regex("[+\\-*/()]")).lastOrNull()
                if (lastNumber?.contains(".") == true) {
                    // Si ya tiene un punto decimal, no añadir otro
                    return
                }
            }

            currentExpression += buttonText
            tvTemp.text = currentExpression
        }
    }
    fun onClickOperator(view: View) {
        if (view is Button) {
            // Verificar si el último carácter es un número antes de agregar operador
            val lastChar = currentExpression.lastOrNull()
            if (lastChar != null && lastChar.isDigit()) {
                val operator = when (view.text) {
                    "X" -> "*"  // Cambiar "X" a "*" para multiplicación
                    "%" -> "/100"
                    else -> view.text
                }
                currentExpression += operator
                tvTemp.text = currentExpression
            } else if (view.text == "-" && currentExpression.isEmpty()) {
                // Permitir el uso de "-" al inicio de la expresión (para números negativos)
                currentExpression += "-"
                tvTemp.text = currentExpression
            }
        }
    }


    // Mantener un contador de paréntesis abiertos
    private var openParenthesesCount = 0



    fun parentesis(view: View) {
        if (view is Button) {
            // Determinar si agregar paréntesis de apertura o cierre
            val lastChar = currentExpression.lastOrNull()

            if (lastChar == null || lastChar in listOf('+', '-', '*', '/', '(', ' ')) {
                // Agregar paréntesis de apertura si es el comienzo de la expresión o después de un operador o un paréntesis de apertura
                currentExpression += "("
                openParenthesesCount++
            } else if (openParenthesesCount > 0 && lastChar !in listOf('+', '-', '*', '/', '(')) {
                // Agregar paréntesis de cierre si hay paréntesis abiertos y el último carácter no es un operador o un paréntesis de apertura
                currentExpression += ")"
                openParenthesesCount--
            } else {
                // Si ninguna condición se cumple, agregar paréntesis de apertura por defecto
                currentExpression += "("
                openParenthesesCount++
            }

            tvTemp.text = currentExpression
        }
    }



    fun onClickEqual(view: View) {
        if (view is Button) {
            try {
                // Cerrar paréntesis abiertos
                var openParentheses = currentExpression.count { it == '(' }
                var closeParentheses = currentExpression.count { it == ')' }
                while (openParentheses > closeParentheses) {
                    currentExpression += ")"
                    closeParentheses++
                }

                // Construir y evaluar la expresión
                val expression = ExpressionBuilder(currentExpression).build()
                val result = expression.evaluate()

                // Mostrar resultado
                tvResult.text = result.toString()
                currentExpression = result.toString()

                // Limpiar tvTemp después de mostrar el resultado
                tvTemp.text = ""
            } catch (e: Exception) {
                tvResult.text = "Error"
            }
        }
    }

    fun onClickClear(view: View) {
        if (view is Button) {
            currentExpression = ""
            tvTemp.text = "0"
            tvResult.text = "0"
        }
    }

    fun onClickSpecial(view: View) {
        if (view is Button) {
            when (view.id) {
                R.id.btnRaiz -> {
                    currentExpression += "sqrt("
                    tvTemp.text = currentExpression
                }
                R.id.btnPi -> {
                    currentExpression += PI.toString()
                    tvTemp.text = currentExpression
                }
                R.id.btnPotencia -> {
                    currentExpression += "^"
                    tvTemp.text = currentExpression
                }
                R.id.btnFactorial -> {
                    // Encontrar el último número ingresado antes del factorial
                    val regex = Regex("""\d+(\.\d+)?""")
                    val matches = regex.findAll(currentExpression)
                    val lastNumber = matches.lastOrNull()?.value ?: ""

                    if (lastNumber.isNotEmpty()) {
                        // Calcular el factorial del último número
                        val number = lastNumber.toInt()
                        var factorialResult = 1
                        for (i in 2..number) {
                            factorialResult *= i
                        }
                        currentExpression = currentExpression.replaceLast(lastNumber, factorialResult.toString())
                        tvTemp.text = currentExpression
                    }
                }
            }
        }
    }

    fun String.replaceLast(oldValue: String, newValue: String): String {
        val lastIndex = this.lastIndexOf(oldValue)
        return if (lastIndex == -1) this else this.substring(0, lastIndex) + newValue + this.substring(lastIndex + oldValue.length)
    }

    fun onDeleteLastCharacter(view: View) {
        if (currentExpression.isNotEmpty()) {
            currentExpression = currentExpression.dropLast(1)
            tvTemp.text = currentExpression
        }
    }


}
