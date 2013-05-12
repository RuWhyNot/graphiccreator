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
		Label1.setBounds(25, 330, 900, 30);
		add(Label1);

		TextArea Area = new TextArea("", 50, 50, TextArea.SCROLLBARS_HORIZONTAL_ONLY);
		Area.setEditable(false);
		Area.setBounds(25, 50, 450, 300);
		add(Area);

		// �������� ���� �����
		TextField Memo = new TextField(300);
		Memo.setBounds(25, 360, 450, 30);
		add(Memo);

		// �������� ������
		Button Btn1 = new Button("�������");
		// ���������� ������
		Btn1.setBounds(25, 420, 100, 30);
		add(Btn1);

		// ���������� ������� Enter
		Memo.addActionListener(new ActLis(Memo, Label1, Area));

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

	public static String VerifyText(String st, TextArea ta)
	{
		// ��������
		final String CMDDRAW_NAME = "��������|����������|���������|������";

		// ���������
		final String IS_NAME = "��������|����������";

		// �������� ��������
		// ����-��������
		final String CLOSED_NAME = "���������|��������|���������";
		final String UNCLOSED_NAME = "����������|�� ���������|�� ��������|�� ���������|���������|�������|���������";
		final String SYMMETRIC_NAME = "������������|�����������|������������|������������";
		// ����������
		final String HASPART_NAME = "����������|����������|����������";
		final String HAS_NAME = "�������|�������|�������";

		// ���������� ��������
		final String BIG_NAME = "�������|�������";
		final String SMALL_NAME = "���������|���������";

		// ������� �������� �����
		final String FIGURE_NAME = "������|������";
		final String ELLIPSE_NAME = "������|�������";
		final String OVAL_NAME = "����";
		final String POLYGON_NAME = "�������|�������������";
		final String POLYLINE_NAME = "���������|�������|���������|��������";
		final String CIRCLE_NAME = "����|����������";
		final String RECT_NAME = "�������������";
		final String SQUARE_NAME = "�������";
		final String TRIANGLE_NAME = "�����������";

		// �������� ����� � ����������� ������
		final String FIGURE_RNAME = "������";
		final String ELLIPSE_RNAME = "������";
		final String OVAL_RNAME = "����";
		final String POLYGON_RNAME = "�������|�������������";
		final String POLYLINE_RNAME = "���������|��������";
		final String CIRCLE_RNAME = "����|����������";
		final String RECT_RNAME = "�������������";
		final String SQUARE_RNAME = "�������";
		final String TRIANGLE_RNAME = "�����������";

		// ----- ��������� ���������
		// �����-���� ������
		final String SOME_FIGURE = FIGURE_NAME+"|"+ELLIPSE_NAME+"|"+POLYGON_NAME+"|"+POLYLINE_NAME+"|"+CIRCLE_NAME+"|"+RECT_NAME+"|"+SQUARE_NAME+"|"+TRIANGLE_NAME+"|"+OVAL_NAME;

		// �����-���� ����-�������� ("������������ �������")
		final String SOME_PREPROPERTY = CLOSED_NAME+"|"+UNCLOSED_NAME+"|"+SYMMETRIC_NAME+"|"+BIG_NAME+"|"+SMALL_NAME;

		// ----- ���������� ���������
		// ���������� ������
		final String WORD_CHAR = "[�-��-�a-zA-Z_0-9]";

		// ����� � �����
		final String HAS_NVERTS = "("+HAS_NAME+")([\\s]|\\.|$)";

		// �����������
		final String PROPOSITION = WORD_CHAR+"[^.]*(\\.|$)";

		// ����� ���������� �������������� ����������
		final String ANY_FIGURE = "(^|[\\s])+("+SOME_FIGURE+")([\\s]|\\.|,|$)";

		// ����-��������
		final String ANY_PREPROPERTY = "(^|[\\s])+("+SOME_PREPROPERTY+")([\\s]|\\.|,|$)";

		// ����-�������� � �������� ������
		final String PROPERTY_FIGURE = "[\\s]*(("+SOME_PREPROPERTY+")[^.]*)+[\\s]+("+SOME_FIGURE+")([\\s]|$)";

		// �������� ��� �����������
		for (int i = 0; i < getCountOfStringsLikeThis(st, PROPOSITION); i++)
		{
			// ��������� ����������� � ����������
			String Propn = getStringLikeThis(st, PROPOSITION, i);

			// ���� �� ����������, �����-���� �����
			if (hasStringLikeThis(Propn, ANY_FIGURE))
			{
				// ������� ����������� � TextArea
				ta.append("�����������: "+Propn+"\n");

				// ������� �������� ���� ����� � TextArea
				String FiguresString = "������: ";
				for (int j = 0; j < getCountOfStringsLikeThis(Propn, ANY_FIGURE); j++)
				{
					// ������� ���, ��������� �� ��������
					FiguresString += getStringLikeThis(getStringLikeThis(Propn, ANY_FIGURE, j), SOME_FIGURE)+" ";
				}
				ta.append(FiguresString+"\n");


				// ���� ���� ����-��������
				if (hasStringLikeThis(Propn, PROPERTY_FIGURE))
				{
					// ���������� ������ �� ����������
					String StrWProper = getStringLikeThis(Propn, PROPERTY_FIGURE);

					// ������� ��� ����-��������
					String PropertiesString = "�������� ������ \""+getStringLikeThis(getStringLikeThis(StrWProper, ANY_FIGURE), SOME_FIGURE)+"\": ";
					for (int j = 0; j < getCountOfStringsLikeThis(StrWProper, ANY_FIGURE); j++)
					{
						// ������� ���, ��������� �� ��������
						PropertiesString += getStringLikeThis(getStringLikeThis(StrWProper, ANY_PREPROPERTY, j), SOME_PREPROPERTY)+" ";
					}
					ta.append(PropertiesString+"\n");
				}
			}
		}

		return "���������� �����������: "+getCountOfStringsLikeThis(st, PROPOSITION);
	}

	// �������� �� ����� ���������, ��������������� �������?
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

	// ������� ���������� ���������, ��������������� �������
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

	// ������� ������ ���������, ������� ������������� �������
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

	// (�������) ������� n-��� ��������� (������� � ����), ������� ������������� �������
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
}

// ��������� ��������
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