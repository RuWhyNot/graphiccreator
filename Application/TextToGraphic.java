import java.awt.*;
import java.awt.event.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 *	В коде использована прямая конкатенация строк, что является неоптимальным решением.
 *	Если, в будущем, приложение планируется использовать для обработки большого количества
 *	строк, то стоит использовать StringBuffer для сложения строк.
 */


// наше окно
class Window1 extends Frame
{
	// массив высот для построения графика
	private int[] Elements = new int[400]; // желательно установить размер после обращения к файлу

	// Инициализация
	Window1(String s)
	{
		super(s);
		// положение окна
		setBounds(300, 250, 100, 30);
		setLayout(null);

		// создание обработчика закрытия окна
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent ev)
			{
				// выводим в консоль
				TextToGraphic.Log("Закрытие приложения");
				System.exit(0);
			}
		});

		// создаём лейбл
		Label Label1 = new Label();
		Label1.setBounds(25, 330, 900, 30);
		add(Label1);

		TextArea Area = new TextArea("", 50, 50, TextArea.SCROLLBARS_BOTH);
		Area.setEditable(false);
		Area.setBounds(25, 50, 450, 300);
		add(Area);

		// создание поля ввода
		TextField Memo = new TextField(300);
		Memo.setBounds(25, 360, 450, 30);
		add(Memo);

		// создание кнопки
		Button Btn1 = new Button("Чертить");
		// размещение кнопки
		Btn1.setBounds(25, 420, 100, 30);
		add(Btn1);

		// обработчик нажатия Enter
		Memo.addActionListener(new ActLis(Memo, Label1, Area));

		// создание обработчика нажатия кнопки
		Btn1.addActionListener(new ActLis(Memo, Label1, Area));
		
		/*Btn1.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				TextToGraphic.Log("Нажали кнопку");

				// перерисовать
				paint(getGraphics());
				
				new ActLis(Memo, Label1, Area)
			}
		});*/
	}

	// процедура перерисовки окна
	public void paint(Graphics g)
	{
		// тут можно рисовать
	}
}

// основной класс
class TextToGraphic
{
	// --------------- КОНСТАНТЫ ------------------
	// комманды
	final static String CMDDRAW_NAME = "рисовать|нарисовать|начертить|рисуем";

	// отношения
	final static String IS_NAME = "является|являющийся";

	// ключевые свойства
	// пред-свойства
	final static String CLOSED_NAME = "замкнутая|замкнута|замкнутой";
	final static String UNCLOSED_NAME = "разомкнута|разомкнутый|не замкнутая|не замкнута|не замкнутой|замкнутый|замкнут|замкнутую";
	final static String SYMMETRIC_NAME = "симметричная|симметрична|симметричный|симметричную";
	// переходные
	final static String HASPART_NAME = "содержащий|содержащая|содержащую";
	final static String HAS_NAME = "имеющий|имеющая|имеющую";

	// неключевые свойства
	final static String BIG_NAME = "большой|большая";
	final static String SMALL_NAME = "маленький|маленькая";

	// простые названия фигур
	final static String FIGURE_NAME = "фигура|фигуру";
	final static String ELLIPSE_NAME = "эллипс|эллипса";
	final static String OVAL_NAME = "овал";
	final static String POLYGON_NAME = "полигон|многоугольник";
	final static String POLYLINE_NAME = "полилиния|ломаная|полилинию|ломанную";
	final static String CIRCLE_NAME = "круг|окружность";
	final static String RECT_NAME = "прямоугольник";
	final static String SQUARE_NAME = "квадрат";
	final static String TRIANGLE_NAME = "треугольник";

	// названия фигур в родительном падеже
	final static String FIGURE_RNAME = "фигуру";
	final static String ELLIPSE_RNAME = "эллипс";
	final static String OVAL_RNAME = "овал";
	final static String POLYGON_RNAME = "полигон|многоугольник";
	final static String POLYLINE_RNAME = "полилинию|ломанную";
	final static String CIRCLE_RNAME = "круг|окружность";
	final static String RECT_RNAME = "прямоугольник";
	final static String SQUARE_RNAME = "квадрат";
	final static String TRIANGLE_RNAME = "треугольник";

	// ----- составные выражения
	// какая-либо фигура
	final static String SOME_FIGURE = FIGURE_NAME+"|"+ELLIPSE_NAME+"|"+POLYGON_NAME+"|"+POLYLINE_NAME+"|"+CIRCLE_NAME+"|"+RECT_NAME+"|"+SQUARE_NAME+"|"+TRIANGLE_NAME+"|"+OVAL_NAME;

	// какое-либо пред-свойство
	final static String SOME_PREPROPERTY = CLOSED_NAME+"|"+UNCLOSED_NAME+"|"+SYMMETRIC_NAME+"|"+BIG_NAME+"|"+SMALL_NAME;

	// ----- регулярные выражения
	// словестный символ
	final static String WORD_CHAR = "[а-яА-Яa-zA-Z_0-9]";

	// имеет № углов
	final static String HAS_NVERTS = "("+HAS_NAME+")([\\s]|\\.|$)";

	// предложение
	final static String PROPOSITION = WORD_CHAR+"[^.]*(\\.|$)";

	// любое упоминание геометрических примитивов
	final static String ANY_FIGURE = "(^|[\\s])+("+SOME_FIGURE+")([\\s]|\\.|,|$)";

	// пред-свойство
	final static String ANY_PREPROPERTY = "(^|[\\s])+("+SOME_PREPROPERTY+")([\\s]|\\.|,|$)+";
	final static String ANY_PREPROPERTY2 = "(^|[\\s])*("+SOME_PREPROPERTY+")([\\s]|\\.|,|$)+";

	// пред-свойства и название фигуры
	final static String PROPERTY_FIGURE = "[\\s]*(("+SOME_PREPROPERTY+")[^.]*)+[\\s]+("+SOME_FIGURE+")([\\s]|\\.|,|$)";

	// выполняется при запуске приложения
	public static void main(String[] args)
	{
		// создаём новое окно и указываем заголовок
		Window1 f = new Window1("Чертить");
		f.setSize(500, 500);
		f.setVisible(true);
	}

	// вывод лога в консоль
	public static void Log(String text)
	{
		System.out.println(new java.text.SimpleDateFormat("HH:mm:ss,S").format(java.util.Calendar.getInstance().getTime())+" Log: "+text);
	}

	public static String VerifyText(String st, TextArea ta)
	{
		// проходим все предложения
		for (int i = 0; i < getCountOfStringsLikeThis(st, PROPOSITION); i++)
		{
			// сохраняем предложение в переменную
			String Propn = getStringLikeThis(st, PROPOSITION, i);

			// если есть упоминание каких-либо фигур
			if (hasStringLikeThis(Propn, ANY_FIGURE))
			{
				// выводим предложение в TextArea
				ta.append("Предложение: "+Propn+"\n");

				// выводим название всех фигур в TextArea
				String FiguresString = "Фигуры: ";
				for (int j = 0; j < getCountOfStringsLikeThis(Propn, ANY_FIGURE); j++)
				{
					// выводим имя, очищенное от символов
					FiguresString += getStringLikeThis(getStringLikeThis(Propn, ANY_FIGURE, j), SOME_FIGURE)+" ";
				}
				ta.append(FiguresString+"\n");


				// если есть пред-свойства
				if (hasStringLikeThis(Propn, PROPERTY_FIGURE))
				{
					String Next_String = Propn;
					String This_String;
					
					// для каждой фигуры
					for (int j = 0; j < getCountOfStringsLikeThis(Propn, ANY_FIGURE); j++)
					{
						This_String = getStartStringLikeThis(Next_String, ANY_FIGURE);
						Next_String = getEndStringLikeThis(Next_String, ANY_FIGURE);

						// если содержатся пред-свойства
						if (hasStringLikeThis(This_String, PROPERTY_FIGURE))
						{
							// выводим все пред-свойства
							String PropertiesString = "Свойства фигуры \""+getStringLikeThis(getStringLikeThis(This_String, ANY_FIGURE), SOME_FIGURE)+"\": ";
							for (int k = 0; k < getCountOfStringsLikeThis(This_String, ANY_PREPROPERTY2); k++)
							{
								// выводим имя, очищенное от символов
								PropertiesString += getStringLikeThis(getStringLikeThis(This_String, ANY_PREPROPERTY2, k), SOME_PREPROPERTY)+" ";
							}
							ta.append(PropertiesString+"\n");
						}
					}
				}
			}
		}

		return "Количество предложений: "+getCountOfStringsLikeThis(st, PROPOSITION);
	}

	// содержит ли текст выражения, соответствующие шаблону?
	public static boolean hasStringLikeThis(String st, String mask)
	{
		Pattern p = Pattern.compile(mask, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(st.toLowerCase());
		while (m.find())
		{
			return true;
		}
		return false;
	}

	// вернуть количество выражений, соответствующих шаблону
	public static int getCountOfStringsLikeThis(String st, String mask)
	{
		int col = 0;
		Pattern p = Pattern.compile(mask, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(st.toLowerCase());
		while (m.find())
		{
			col++;
		}
		return col;
	}

	// вернуть первое выражение, которое соответствует шаблону
	public static String getStringLikeThis(String st, String mask)
	{
		Pattern p = Pattern.compile(mask, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(st.toLowerCase());
		while (m.find())
		{
			return m.group();
		}
		return "";
	}

	// (синоним) вернуть n-ное выражение (начиная с нуля), которое соответствует шаблону
	public static String getStringLikeThis(String st, String mask, int ordNumber)
	{
		int col = 0;
		Pattern p = Pattern.compile(mask, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(st.toLowerCase());
		while (m.find())
		{
			if (col == ordNumber)
				return m.group();

			col++;
		}
		return "";
	}

	// узнать индекс символа начала вхождения n-ного выражения, соответствующего шаблону
	public static int getStartPosStringLikeThis(String st, String mask, int ordNumber)
	{
		int col = 0;
		Pattern p = Pattern.compile(mask, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(st.toLowerCase());
		while (m.find())
		{
			if (col == ordNumber)
			{
				return m.start();
			}

			col++;
		}
		return -1;
	}

	// узнать индекс символа конца вхождения n-ного выражения, соответствующего шаблону
	public static int getEndPosStringLikeThis(String st, String mask, int ordNumber)
	{
		int col = 0;
		Pattern p = Pattern.compile(mask, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(st.toLowerCase());
		while (m.find())
		{
			if (col == ordNumber)
			{
				return m.end();
			}

			col++;
		}
		return -1;
	}

	// вернуть строку, которая предшествует первому выражению, соответствующему шаблону
	public static String getStartStringLikeThis(String st, String mask)
	{
		int end = getEndPosStringLikeThis(st, mask, 0);
		if (end != -1)
			return st.substring(0, end);
		else
			return st;
	}

	// вернуть строку, которая предшествует первому выражению, соответствующему шаблону
	public static String getEndStringLikeThis(String st, String mask)
	{
		int end = getEndPosStringLikeThis(st, mask, 0);
		if (end != -1)
			return st.substring(end, st.length());
		else
			return st;
	}

	// (синоним) вернуть строку, которая предшествует n-ному выражению, соответствующему шаблону
	public static String getStartStringLikeThis(String st, String mask, int ordNumber)
	{
		int end = getEndPosStringLikeThis(st, mask, ordNumber);
		if (end != -1)
			return st.substring(0, end);
		else
			return st;
	}

	// (синоним) вернуть строку, которая предшествует n-ному выражению, соответствующему шаблону
	public static String getEndStringLikeThis(String st, String mask, int ordNumber)
	{
		int end = getEndPosStringLikeThis(st, mask, ordNumber);
		if (end != -1)
			return st.substring(end, st.length());
		else
			return st;
	}
}

// обработка действий
class ActLis implements ActionListener
{
	private TextField tf;
	private Label lb;
	private TextArea ta;

	ActLis(TextField tf, Label lb, TextArea ta)
	{
		this.tf = tf;
		this.lb = lb;
		this.ta = ta;
	}

	public void actionPerformed(ActionEvent ae)
	{
		ta.replaceRange("", 0, 10000);
		lb.setText(TextToGraphic.VerifyText(tf.getText(), ta));
		tf.setText("");
	}
}