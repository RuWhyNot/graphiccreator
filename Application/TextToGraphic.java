import java.awt.*;
import java.awt.event.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		Label1.setBounds(30, 330, 400, 30);
		add(Label1);

		// создание поля ввода
		TextField Memo = new TextField(300);
		Memo.setBounds(30, 360, 300, 30);
		add(Memo);

		// создание кнопки
		Button Btn1 = new Button("Чертить");
		// размещение кнопки
		Btn1.setBounds(30, 420, 100, 30);
		add(Btn1);

		// обработчик нажатия Enter
		Memo.addActionListener(new ActLis(Memo, Label1));

		// создание обработчика нажатия кнопки
		Btn1.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				TextToGraphic.Log("Нажали кнопку");

				// перерисовать
				paint(getGraphics());
			}
		});
	}

	// процедура перерисовки окна
	public void paint(Graphics g)
	{
		drawGraph(g, 50, 200);
	}

	public void drawGraph(Graphics g, int xDr, int yDr)
	{
		/*
		// оси координат
		g.drawLine(xDr, yDr, xDr + 400, yDr);
		g.drawLine(xDr, yDr - 150, xDr, yDr + 150);

		// чертить массив Y от i
		for (int i = 1; i < 400; i++)
		{
			g.drawLine(xDr + i - 1, yDr - Elements[i - 1], xDr + i, yDr - Elements[i]);
		}*/
	}
}

// основной класс
class TextToGraphic
{

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

	public static String VerifyText(String st)
	{
		// комманды
		final String DRAW_NAME = "рисовать|нарисовать|начертить|рисуем";

		// отношения
		final String IS_NAME = "является|являющийся|который является|-";

		// свойства
		final String CLOSED_NAME = "замкнутая|замкнута|замкнутой";
		final String UNCLOSED_NAME = "разомкнута|не замкнутая|не замкнута|не замкнутой|замкнутый|замкнут|замкнутую";
		final String HASPART_NAME = "состоит из|имеет в составе|содержит";
		final String SYMMETRIC_NAME = "симметричная|симметрична|симметричный|симметричную";

		// фигуры
		final String FIGURE_NAME = "фигура|фигуру";
		final String ELLIPSE_NAME = "эллипс";
		final String POLYGON_NAME = "полигон|многоугольник";
		final String POLYLINE_NAME = "полилиния|ломаная";
		final String CIRCLE_NAME = "круг|окружность";
		final String RECT_NAME = "прямоугольник";
		final String SQUARE_NAME = "квадрат";
		final String TRIANGLE_NAME = "треугольник";

		// составные выражения
		// какая-либо фигура 
		final String SOME_FIGURE = FIGURE_NAME+"|"+ELLIPSE_NAME+"|"+POLYGON_NAME+"|"+POLYLINE_NAME+"|"+CIRCLE_NAME+"|"+RECT_NAME+"|"+SQUARE_NAME+"|"+TRIANGLE_NAME;
		
		// какое-либо пред-свойство (симметричный квадрат)
		final String SOME_PREPROPERTY = CLOSED_NAME+"|"+UNCLOSED_NAME+"|"+SYMMETRIC_NAME;

		// регулярные выражения
		// есть ли в тексте любая команда
		final String ANY_COMMAND = "[^.]*("+DRAW_NAME+")[^.]+("+SOME_FIGURE+")(\\.|$)";

		// есть ли в тексте простая команда
		final String SIMPLE_COMMAND = "[^.]*("+DRAW_NAME+")[\\s]+("+SOME_FIGURE+")(\\.|$)";

		// рисовать+описание+название фигуры
		final String ADV_COMMAND = "[^.]*("+DRAW_NAME+")+[\\s]+("+SOME_PREPROPERTY+")+[\\s]+("+SOME_FIGURE+")(\\.|$)";

		// смотрим, есть ли команда
		if (IsStringLikeThis(st, ANY_COMMAND))
		{
			// ищем простую комманду
			if (IsStringLikeThis(st, SIMPLE_COMMAND))
			{
				return "Команда: "+st;
			}
			else if (IsStringLikeThis(st, ADV_COMMAND)) // если такой нет, ищем более сложную команду
			{
				return "Команда с условиями: "+st;
			}
		}
		return "";
	}
	
	public static boolean IsStringLikeThis(String st, String mask)
	{
		Pattern p = Pattern.compile(mask, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(st);
		while (m.find())
		{
			return true;
		}
		return false;
	}
}

// обработка действий
class ActLis implements ActionListener
{
	private TextField tf;
	private Label lb;

	ActLis(TextField tf, Label lb)
	{
		this.tf = tf;
		this.lb = lb;
	}

	public void actionPerformed(ActionEvent ae)
	{
		lb.setText(TextToGraphic.VerifyText(tf.getText()));
		tf.setText("");
	}
}