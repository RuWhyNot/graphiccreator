import java.awt.*;
import java.awt.event.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// ���� ����
class Window1 extends Frame
{
	// ������ ����� ��� ���������� �������
	private int[] Elements = new int[400]; // ���������� ���������� ������ ����� ��������� � �����

	// �������������
	Window1(String s)
	{
		super(s);
		// ��������� ����
		setBounds(300, 250, 100, 30);
		setLayout(null);

		// �������� ����������� �������� ����
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent ev)
			{
				// ������� � �������
				TextToGraphic.Log("�������� ����������");
				System.exit(0);
			}
		});

		// ������ �����
		Label Label1 = new Label();
		Label1.setBounds(30, 330, 400, 30);
		add(Label1);

		// �������� ���� �����
		TextField Memo = new TextField(300);
		Memo.setBounds(30, 360, 300, 30);
		add(Memo);

		// �������� ������
		Button Btn1 = new Button("�������");
		// ���������� ������
		Btn1.setBounds(30, 420, 100, 30);
		add(Btn1);

		// ���������� ������� Enter
		Memo.addActionListener(new ActLis(Memo, Label1));

		// �������� ����������� ������� ������
		Btn1.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				TextToGraphic.Log("������ ������");

				// ������������
				paint(getGraphics());
			}
		});
	}

	// ��������� ����������� ����
	public void paint(Graphics g)
	{
		drawGraph(g, 50, 200);
	}

	public void drawGraph(Graphics g, int xDr, int yDr)
	{
		/*
		// ��� ���������
		g.drawLine(xDr, yDr, xDr + 400, yDr);
		g.drawLine(xDr, yDr - 150, xDr, yDr + 150);

		// ������� ������ Y �� i
		for (int i = 1; i < 400; i++)
		{
			g.drawLine(xDr + i - 1, yDr - Elements[i - 1], xDr + i, yDr - Elements[i]);
		}*/
	}
}

// �������� �����
class TextToGraphic
{

	// ����������� ��� ������� ����������
	public static void main(String[] args)
	{
		// ������ ����� ���� � ��������� ���������
		Window1 f = new Window1("�������");
		f.setSize(500, 500);
		f.setVisible(true);
	}

	// ����� ���� � �������
	public static void Log(String text)
	{
		System.out.println(new java.text.SimpleDateFormat("HH:mm:ss,S").format(java.util.Calendar.getInstance().getTime())+" Log: "+text);
	}

	public static String VerifyText(String st)
	{
		// ��������
		final String DRAW_NAME = "��������|����������|���������|������";

		// ���������
		final String IS_NAME = "��������|����������|������� ��������|-";

		// ��������
		final String CLOSED_NAME = "���������|��������|���������";
		final String UNCLOSED_NAME = "����������|�� ���������|�� ��������|�� ���������|���������|�������|���������";
		final String HASPART_NAME = "������� ��|����� � �������|��������";
		final String SYMMETRIC_NAME = "������������|�����������|������������|������������";

		// ������
		final String FIGURE_NAME = "������|������";
		final String ELLIPSE_NAME = "������";
		final String POLYGON_NAME = "�������|�������������";
		final String POLYLINE_NAME = "���������|�������";
		final String CIRCLE_NAME = "����|����������";
		final String RECT_NAME = "�������������";
		final String SQUARE_NAME = "�������";
		final String TRIANGLE_NAME = "�����������";

		// ��������� ���������
		// �����-���� ������ 
		final String SOME_FIGURE = FIGURE_NAME+"|"+ELLIPSE_NAME+"|"+POLYGON_NAME+"|"+POLYLINE_NAME+"|"+CIRCLE_NAME+"|"+RECT_NAME+"|"+SQUARE_NAME+"|"+TRIANGLE_NAME;
		
		// �����-���� ����-�������� (������������ �������)
		final String SOME_PREPROPERTY = CLOSED_NAME+"|"+UNCLOSED_NAME+"|"+SYMMETRIC_NAME;

		// ���������� ���������
		// ���� �� � ������ ����� �������
		final String ANY_COMMAND = "[^.]*("+DRAW_NAME+")[^.]+("+SOME_FIGURE+")(\\.|$)";

		// ���� �� � ������ ������� �������
		final String SIMPLE_COMMAND = "[^.]*("+DRAW_NAME+")[\\s]+("+SOME_FIGURE+")(\\.|$)";

		// ��������+��������+�������� ������
		final String ADV_COMMAND = "[^.]*("+DRAW_NAME+")+[\\s]+("+SOME_PREPROPERTY+")+[\\s]+("+SOME_FIGURE+")(\\.|$)";

		// �������, ���� �� �������
		if (IsStringLikeThis(st, ANY_COMMAND))
		{
			// ���� ������� ��������
			if (IsStringLikeThis(st, SIMPLE_COMMAND))
			{
				return "�������: "+st;
			}
			else if (IsStringLikeThis(st, ADV_COMMAND)) // ���� ����� ���, ���� ����� ������� �������
			{
				return "������� � ���������: "+st;
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

// ��������� ��������
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