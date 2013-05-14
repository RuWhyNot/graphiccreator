import java.awt.*;
import java.awt.event.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Vector;

/*
 *	� ���� ������������ ������ ������������ �����, ��� �������� ������������� ��������.
 *	����, � �������, ���������� ����������� ������������ ��� ��������� �������� ����������
 *	�����, �� ����� ������������ StringBuffer ��� �������� �����.
 */

class Figure
{
	// ��� �� ������
	int Class;
	// ������
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

// ���� ����
class Window1 extends Frame
{
	// ������ ����� ��� ���������� �������
	private int[] Elements = new int[400]; // ���������� ���������� ������ ����� ��������� � �����

	// ������ �����
	FiguresMass Figures = new FiguresMass();

	// ������� ����
	TextField Field;
	Label Label1;
	TextArea Area;
	
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
		Label1 = new Label();
		Label1.setBounds(525, 360, 1000, 30);
		add(Label1);

		// ���� ������
		Area = new TextArea("", 50, 50, TextArea.SCROLLBARS_BOTH);
		Area.setEditable(false);
		Area.setBounds(525, 50, 450, 300);
		add(Area);

		// �������� ���� �����
		Field = new TextField(300);
		Field.setBounds(25, 360, 450, 30);
		add(Field);

		// �������� ������
		Button Btn1 = new Button("��������");
		// ���������� ������
		Btn1.setBounds(25, 420, 100, 30);
		add(Btn1);

		// ���������� ������� Enter
		Field.addActionListener(new ActLis(this));
		Field.addTextListener(new ActLis(this));

		// �������� ����������� ������� ������
		Btn1.addActionListener(new ActLis(this));
	}

	// ��������� ����������� ����
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
		// ������������ 0 - ���������, 1 - ����������, 2 �������
		Wid = (int)Math.round(Wid * (size + 1) / 3.0);

		// ������ ��������� ������
		switch (figure)
		{
			case 0: // ����
				g.drawOval(xPos, yPos, Wid, Wid);
				break;
			case 1: // �������
				g.drawRect(xPos, yPos, Wid, Wid);
				break;
			case 2: // �������������
				g.drawRect(xPos, yPos + Wid / 4, Wid, Wid / 2);
				break;
			case 3: // ����
				g.drawOval(xPos, yPos + Wid / 4, Wid, Wid / 2);
				break;
			case 4: // ������
				g.drawOval(xPos, yPos + Wid / 4, Wid, Wid / 2);
				break;
			case 5: // �����������
				int[] arrX = {xPos, xPos + Wid, xPos + Wid / 2};
				int[] arrY = {yPos + Wid - Wid / 4, yPos + Wid - Wid / 4, yPos + Wid / 2 - Wid / 4};
				g.drawPolygon(arrX, arrY, 3);
				break;
			default:
				break;
		}
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
		f.setSize(1000, 500);
		f.setVisible(true);
	}

	// ����� ���� � �������
	public static void Log(String text)
	{
		System.out.println(new java.text.SimpleDateFormat("HH:mm:ss,S").format(java.util.Calendar.getInstance().getTime())+" Log: "+text);
	}

	public static String VerifyText(String st, TextArea ta, FiguresMass fr)
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
				String FigureName = "";
				for (int j = 0; j < getCountOfStringsLikeThis(Propn, ANY_FIGURE); j++)
				{
					// ������� ���, ��������� �� ��������
					FigureName = getStringLikeThis(getStringLikeThis(Propn, ANY_FIGURE, j), SOME_FIGURE);
					fr.insertFigure(new Figure(getFigureID(FigureName)));
					FiguresString += FigureName + " ";
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

	// ������� ���������� ���������, ��������������� �������
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

	// ������� ������ ���������, ������� ������������� �������
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

	// (�������) ������� n-��� ��������� (������� � ����), ������� ������������� �������
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

	// ������ ������ ������� ������ ��������� n-���� ���������, ���������������� �������
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

	// ������ ������ ������� ����� ��������� n-���� ���������, ���������������� �������
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

	// ������� ������, ������� ������������ ������� ���������, ���������������� �������
	private static String getStartStringLikeThis(String st, String mask)
	{
		int end = getEndPosStringLikeThis(st, mask, 0);
		if (end != -1)
			return st.substring(0, end);
		else
			return st;
	}

	// ������� ������, ������� ������������ ������� ���������, ���������������� �������
	private static String getEndStringLikeThis(String st, String mask)
	{
		int end = getEndPosStringLikeThis(st, mask, 0);
		if (end != -1)
			return st.substring(end, st.length());
		else
			return st;
	}

	// (�������) ������� ������, ������� ������������ n-���� ���������, ���������������� �������
	private static String getStartStringLikeThis(String st, String mask, int ordNumber)
	{
		int end = getEndPosStringLikeThis(st, mask, ordNumber);
		if (end != -1)
			return st.substring(0, end);
		else
			return st;
	}

	// (�������) ������� ������, ������� ������������ n-���� ���������, ���������������� �������
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

// ��������� ��������
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
	
	// ��� ������� Enter ��� ������
	public void actionPerformed(ActionEvent ae)
	{
		ta.replaceRange("", 0, 10000);
		fr.reset();
		//lb.setText(TextToGraphic.VerifyText(tf.getText(), ta, fr));
		tf.setText("");
		window.update(window.getGraphics());
		window.paint(window.getGraphics());
	}

	// ��� ��������� ������
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