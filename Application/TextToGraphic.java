import java.awt.*;
import java.awt.event.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 *	� ���� ������������ ������ ������������ �����, ��� �������� ������������� ��������.
 *	����, � �������, ���������� ����������� ������������ ��� ��������� �������� ����������
 *	�����, �� ����� ������������ StringBuffer ��� �������� �����.
 */


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

		TextArea Area = new TextArea("", 50, 50, TextArea.SCROLLBARS_BOTH);
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
		Btn1.addActionListener(new ActLis(Memo, Label1, Area));
		
		/*Btn1.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				TextToGraphic.Log("������ ������");

				// ������������
				paint(getGraphics());
				
				new ActLis(Memo, Label1, Area)
			}
		});*/
	}

	// ��������� ����������� ����
	public void paint(Graphics g)
	{
		// ��� ����� ��������
	}
}

// �������� �����
class TextToGraphic
{
	// --------------- ��������� ------------------
	// ��������
	final static String CMDDRAW_NAME = "��������|����������|���������|������";

	// ���������
	final static String IS_NAME = "��������|����������";

	// �������� ��������
	// ����-��������
	final static String CLOSED_NAME = "���������|��������|���������";
	final static String UNCLOSED_NAME = "����������|�����������|�� ���������|�� ��������|�� ���������|���������|�������|���������";
	final static String SYMMETRIC_NAME = "������������|�����������|������������|������������";
	// ����������
	final static String HASPART_NAME = "����������|����������|����������";
	final static String HAS_NAME = "�������|�������|�������";

	// ���������� ��������
	final static String BIG_NAME = "�������|�������";
	final static String SMALL_NAME = "���������|���������";

	// ������� �������� �����
	final static String FIGURE_NAME = "������|������";
	final static String ELLIPSE_NAME = "������|�������";
	final static String OVAL_NAME = "����";
	final static String POLYGON_NAME = "�������|�������������";
	final static String POLYLINE_NAME = "���������|�������|���������|��������";
	final static String CIRCLE_NAME = "����|����������";
	final static String RECT_NAME = "�������������";
	final static String SQUARE_NAME = "�������";
	final static String TRIANGLE_NAME = "�����������";

	// �������� ����� � ����������� ������
	final static String FIGURE_RNAME = "������";
	final static String ELLIPSE_RNAME = "������";
	final static String OVAL_RNAME = "����";
	final static String POLYGON_RNAME = "�������|�������������";
	final static String POLYLINE_RNAME = "���������|��������";
	final static String CIRCLE_RNAME = "����|����������";
	final static String RECT_RNAME = "�������������";
	final static String SQUARE_RNAME = "�������";
	final static String TRIANGLE_RNAME = "�����������";

	// ----- ��������� ���������
	// �����-���� ������
	final static String SOME_FIGURE = FIGURE_NAME+"|"+ELLIPSE_NAME+"|"+POLYGON_NAME+"|"+POLYLINE_NAME+"|"+CIRCLE_NAME+"|"+RECT_NAME+"|"+SQUARE_NAME+"|"+TRIANGLE_NAME+"|"+OVAL_NAME;

	// �����-���� ����-��������
	final static String SOME_PREPROPERTY = CLOSED_NAME+"|"+UNCLOSED_NAME+"|"+SYMMETRIC_NAME+"|"+BIG_NAME+"|"+SMALL_NAME;

	// ----- ���������� ���������
	// ���������� ������
	final static String WORD_CHAR = "[�-��-�a-zA-Z_0-9]";

	// ����� � �����
	final static String HAS_NVERTS = "("+HAS_NAME+")([\\s]|\\.|$)";

	// �����������
	final static String PROPOSITION = WORD_CHAR+"[^.]*(\\.|$)";

	// ����� ���������� �������������� ����������
	final static String ANY_FIGURE = "(^|[\\s])+("+SOME_FIGURE+")([\\s]|\\.|,|$)";

	// ����-��������
	final static String ANY_PREPROPERTY = "(^|[\\s])+("+SOME_PREPROPERTY+")([\\s]|\\.|,|$)+";
	final static String ANY_PREPROPERTY2 = "(^|[\\s])*("+SOME_PREPROPERTY+")([\\s]|\\.|,|$)+";

	// ����-�������� � �������� ������
	final static String PROPERTY_FIGURE = "[\\s]*(("+SOME_PREPROPERTY+")[^.]*)+[\\s]+("+SOME_FIGURE+")([\\s]|\\.|,|$)";

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
		// �������� ��� �����������
		for (int i = 0; i < getCountOfStringsLikeThis(st, PROPOSITION); i++)
		{
			// ��������� ����������� � ����������
			String Propn = getStringLikeThis(st, PROPOSITION, i);

			// ���� ���� ���������� �����-���� �����
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
					String Next_String = Propn;
					String This_String;
					
					// ��� ������ ������
					for (int j = 0; j < getCountOfStringsLikeThis(Propn, ANY_FIGURE); j++)
					{
						This_String = getStartStringLikeThis(Next_String, ANY_FIGURE);
						Next_String = getEndStringLikeThis(Next_String, ANY_FIGURE);

						// ���� ���������� ����-��������
						if (hasStringLikeThis(This_String, PROPERTY_FIGURE))
						{
							// ������� ��� ����-��������
							String PropertiesString = "�������� ������ \""+getStringLikeThis(getStringLikeThis(This_String, ANY_FIGURE), SOME_FIGURE)+"\": ";
							for (int k = 0; k < getCountOfStringsLikeThis(This_String, ANY_PREPROPERTY2); k++)
							{
								// ������� ���, ��������� �� ��������
								PropertiesString += getStringLikeThis(getStringLikeThis(This_String, ANY_PREPROPERTY2, k), SOME_PREPROPERTY)+" ";
							}
							ta.append(PropertiesString+"\n");
						}
					}
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

	// ������ ������ ������� ������ ��������� n-���� ���������, ���������������� �������
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

	// ������ ������ ������� ����� ��������� n-���� ���������, ���������������� �������
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

	// ������� ������, ������� ������������ ������� ���������, ���������������� �������
	public static String getStartStringLikeThis(String st, String mask)
	{
		int end = getEndPosStringLikeThis(st, mask, 0);
		if (end != -1)
			return st.substring(0, end);
		else
			return st;
	}

	// ������� ������, ������� ������������ ������� ���������, ���������������� �������
	public static String getEndStringLikeThis(String st, String mask)
	{
		int end = getEndPosStringLikeThis(st, mask, 0);
		if (end != -1)
			return st.substring(end, st.length());
		else
			return st;
	}

	// (�������) ������� ������, ������� ������������ n-���� ���������, ���������������� �������
	public static String getStartStringLikeThis(String st, String mask, int ordNumber)
	{
		int end = getEndPosStringLikeThis(st, mask, ordNumber);
		if (end != -1)
			return st.substring(0, end);
		else
			return st;
	}

	// (�������) ������� ������, ������� ������������ n-���� ���������, ���������������� �������
	public static String getEndStringLikeThis(String st, String mask, int ordNumber)
	{
		int end = getEndPosStringLikeThis(st, mask, ordNumber);
		if (end != -1)
			return st.substring(end, st.length());
		else
			return st;
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