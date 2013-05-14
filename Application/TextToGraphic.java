import java.awt.*;
import java.awt.event.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Vector;

/*
 *	В коде использована прямая конкатенация строк, что является неоптимальным решением.
 *	Если, в будущем, приложение планируется использовать для обработки большого количества
 *	строк, то стоит использовать StringBuffer для сложения строк.
 */

class Figure
{
	// что за фигура
	int Class;
	// размер
	int Size;

	Figure(int Class)
	{
		this.Class = Class;
		Size = 1;
	}

	Figure(int Class, int Size)
	{
		this.Class = Class;
		this.Size = Size;
	}
}

class FiguresMass
{
	private Figure[] Mass;
	private int Count;

	FiguresMass()
	{
		Mass = new Figure[50];
		Count = 0;
	}

	public void reset()
	{
		Count = 0;
	}

	public int count()
	{
		return Count;
	}

	public Figure getFigure(int pos)
	{
		return Mass[pos];
	}

	public void insertFigure(Figure fig)
	{
		Mass[Count] = fig;
		Count++;
	}
}

// наше окно
class Window1 extends Frame
{
	// массив высот для построения графика
	private int[] Elements = new int[400]; // желательно установить размер после обращения к файлу

	// массив фигур
	FiguresMass Figures = new FiguresMass();

	// объекты окна
	TextField Field;
	Label Label1;
	TextArea Area;
	
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
		Label1 = new Label();
		Label1.setBounds(525, 360, 1000, 30);
		add(Label1);

		// поле вывода
		Area = new TextArea("", 50, 50, TextArea.SCROLLBARS_BOTH);
		Area.setEditable(false);
		Area.setBounds(525, 50, 450, 300);
		add(Area);

		// создание поля ввода
		Field = new TextField(300);
		Field.setBounds(25, 360, 450, 30);
		add(Field);

		// создание кнопки
		Button Btn1 = new Button("Очистить");
		// размещение кнопки
		Btn1.setBounds(25, 420, 100, 30);
		add(Btn1);

		// обработчик нажатия Enter
		Field.addActionListener(new ActLis(this));
		Field.addTextListener(new ActLis(this));

		// создание обработчика нажатия кнопки
		Btn1.addActionListener(new ActLis(this));
	}

	// процедура перерисовки окна
	public void paint(Graphics g)
	{
		int FiguresCount = Figures.count();
		int xPos = 0, yPos = 0, xSize = 1, ySize = 0, Wid = 0;
		int ImageX = 100, ImageY = 50, ImageSize = 300;

		boolean isFinish = false;
		while (!isFinish)
		{
			if (FiguresCount <= xSize * xSize)
			{
				ySize = FiguresCount / xSize + 1;
				Wid = ImageSize/xSize;
				isFinish = true;
			}
			else
				xSize++;
		}

		for (int i = 0; i < FiguresCount; i++)
		{
			xPos = i%xSize;
			yPos = i/xSize;
			drawFigure(g, Figures.getFigure(i).Class, Figures.getFigure(i).Size, xPos * Wid + ImageX, yPos * Wid + ImageY, (int)Math.round(Wid * 0.9));
		}
	}

	void drawFigure(Graphics g, int figure, int size, int xPos, int yPos, int Wid)
	{
		// масштабируем 0 - маленький, 1 - нормальный, 2 большой
		Wid = (int)Math.round(Wid * (size + 1) / 3.0);

		// рисуем выбранную фигуру
		switch (figure)
		{
			case 0: // круг
				g.drawOval(xPos, yPos, Wid, Wid);
				break;
			case 1: // квадрат
				g.drawRect(xPos, yPos, Wid, Wid);
				break;
			case 2: // прямоугольник
				g.drawRect(xPos, yPos + Wid / 4, Wid, Wid / 2);
				break;
			case 3: // овал
				g.drawOval(xPos, yPos + Wid / 4, Wid, Wid / 2);
				break;
			case 4: // эллипс
				g.drawOval(xPos, yPos + Wid / 4, Wid, Wid / 2);
				break;
			case 5: // треугольник
				int[] arrX = {xPos, xPos + Wid, xPos + Wid / 2};
				int[] arrY = {yPos + Wid - Wid / 4, yPos + Wid - Wid / 4, yPos + Wid / 2 - Wid / 4};
				g.drawPolygon(arrX, arrY, 3);
				break;
			default:
				break;
		}
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
		f.setSize(1000, 500);
		f.setVisible(true);
	}

	// вывод лога в консоль
	public static void Log(String text)
	{
		System.out.println(new java.text.SimpleDateFormat("HH:mm:ss,S").format(java.util.Calendar.getInstance().getTime())+" Log: "+text);
	}

	public static String VerifyText(String st, TextArea ta, FiguresMass fr)
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
				String FigureName = "";
				for (int j = 0; j < getCountOfStringsLikeThis(Propn, ANY_FIGURE); j++)
				{
					// выводим имя, очищенное от символов
					FigureName = getStringLikeThis(getStringLikeThis(Propn, ANY_FIGURE, j), SOME_FIGURE);
					fr.insertFigure(new Figure(getFigureID(FigureName)));
					FiguresString += FigureName + " ";
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
	private static boolean hasStringLikeThis(String st, String mask)
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
	private static int getCountOfStringsLikeThis(String st, String mask)
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
	private static String getStringLikeThis(String st, String mask)
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
	private static String getStringLikeThis(String st, String mask, int ordNumber)
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
	private static int getStartPosStringLikeThis(String st, String mask, int ordNumber)
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
	private static int getEndPosStringLikeThis(String st, String mask, int ordNumber)
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
	private static String getStartStringLikeThis(String st, String mask)
	{
		int end = getEndPosStringLikeThis(st, mask, 0);
		if (end != -1)
			return st.substring(0, end);
		else
			return st;
	}

	// вернуть строку, которая предшествует первому выражению, соответствующему шаблону
	private static String getEndStringLikeThis(String st, String mask)
	{
		int end = getEndPosStringLikeThis(st, mask, 0);
		if (end != -1)
			return st.substring(end, st.length());
		else
			return st;
	}

	// (синоним) вернуть строку, которая предшествует n-ному выражению, соответствующему шаблону
	private static String getStartStringLikeThis(String st, String mask, int ordNumber)
	{
		int end = getEndPosStringLikeThis(st, mask, ordNumber);
		if (end != -1)
			return st.substring(0, end);
		else
			return st;
	}

	// (синоним) вернуть строку, которая предшествует n-ному выражению, соответствующему шаблону
	private static String getEndStringLikeThis(String st, String mask, int ordNumber)
	{
		int end = getEndPosStringLikeThis(st, mask, ordNumber);
		if (end != -1)
			return st.substring(end, st.length());
		else
			return st;
	}

	private static int getFigureID(String FigureName)
	{
		if (hasStringLikeThis(FigureName, CIRCLE_NAME))
			return 0;
		else if (hasStringLikeThis(FigureName, SQUARE_NAME))
			return 1;
		else if (hasStringLikeThis(FigureName, RECT_NAME))
			return 2;
		else if (hasStringLikeThis(FigureName, OVAL_NAME))
			return 3;
		else if (hasStringLikeThis(FigureName, ELLIPSE_NAME))
			return 4;
		else if (hasStringLikeThis(FigureName, TRIANGLE_NAME))
			return 5;
		Log("\""+FigureName+"\"");

		return -1;
	}
}

// обработка действий
class ActLis implements ActionListener, TextListener
{
	private TextField tf;
	private Label lb;
	private TextArea ta;
	private FiguresMass fr;
	private Window1 window;

	ActLis(Window1 window)
	{
		this.window = window;
		this.tf = window.Field;
		this.lb = window.Label1;
		this.ta = window.Area;
		this.fr = window.Figures;
	}
	
	// при нажатии Enter или кнопки
	public void actionPerformed(ActionEvent ae)
	{
		ta.replaceRange("", 0, 10000);
		fr.reset();
		//lb.setText(TextToGraphic.VerifyText(tf.getText(), ta, fr));
		tf.setText("");
		window.update(window.getGraphics());
		window.paint(window.getGraphics());
	}

	// при изменении текста
	public void textValueChanged(TextEvent e)
	{
		ta.replaceRange("", 0, 10000);
		fr.reset();
		lb.setText(TextToGraphic.VerifyText(tf.getText(), ta, fr));
		//tf.setText("");
		window.update(window.getGraphics());
		window.paint(window.getGraphics());
	}
}