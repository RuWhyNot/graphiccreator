import java.awt.*;
import java.awt.event.*;
import java.sql.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 *	� ���� ������������ ������ ������������ �����, ��� �������� ������������� ��������.
 *	����, � �������, ���������� ����������� ������������ ��� ��������� ������� �������,
 *	�� ����� ������������ StringBuffer ��� �������� �����.
 */

// ��������
class Property
{
	public enum PropertyType
	{
		PT_SIZE, // 0 - ���������, 1 - �������, 2 - �������
		PT_SYMMETRIC, // 0 - �� �����������, 1 - ���������� ������������ ������, 2 - ����������� ������������ ���� ���������������� ������
		PT_CLOSED, // 0 - ���������, 1 - �������
		PT_HASVERTS, // ������ ���������� ������
		PT_MINVERTS, // ����� ��� ������� ��� ���������� ������
		PT_MAXVERTS, // ����� �������� ��� ���������� ������
		PT_NONE // ������ �������� (�� ��������������, ��� �� ����� ��� ��������� �����)
	};

	// ��� ��������
	private PropertyType Type;
	// �������� ��������
	private int Value = -1;

	// �������������
	Property(PropertyType Type)
	{
		this.Type = Type;
	}

	// ������������� �� ���������
	Property(PropertyType Type, int Value)
	{
		this.Type = Type;
		this.Value = Value;
	}

	// ������������� c �������� ��������� ���� ��������
	Property(int Type)
	{
		PropertyType[] PType = PropertyType.values();
		this.Type = PType[Type];
	}

	// ������������� c �������� ��������� ���� �������� � �� ��������� ��������
	Property(int Type, int Value)
	{
		PropertyType[] PType = PropertyType.values();
		this.Type = PType[Type];
		this.Value = Value;
	}

	// ������������� ��������-�������� (���� �������� �� ����������)
	Property()
	{
		Type = PropertyType.PT_NONE;
	}

	public void setValue(int Value)
	{
		this.Value = Value;
	}

	// �������� ��� ��������
	public PropertyType type()
	{
		return Type;
	}

	public int value()
	{
		return Value;
	}
}

// ����������� �����, �������� ������ � �����������
class VFigure
{
	// ��� �� ������
	protected int Class;

	// ������ �������
	protected Property[] Properties = new Property[10];
	protected int PropCount;

	// �������� ����� �������� ������
	public void addProperty(Property Prope)
	{
		// ���� �������� � �������� �������� ������
		if (Prope.type() != Property.PropertyType.PT_NONE && Prope.value() != -1)
		{
			boolean duplicateOrAbort = false;

			// ����, �� ���� �� ��� ������ ��������
			for (int i = 0; i < PropCount; i++)
			{
				// ���� ����� �������� ����
				if (Properties[i].type() == Prope.type())
				{
					if (
						Properties[i].type() == Property.PropertyType.PT_SYMMETRIC
						&&
						Properties[i].value() == 1
						&&
						Prope.value() > 0
						)
					{	// ���� ������ ���� ����������� ������������ ������
						// ��������� �������������� ������������ ���� ����������������� ������
						Properties[i].setValue(2);
					}
					else if (
							Properties[i].type() == Property.PropertyType.PT_MINVERTS
							&&
							Properties[i].value() < Prope.value()
							)
					{	// ���� ����������� ��������� ������ ����������� ������������
						Properties[i].setValue(Prope.value());
					}
					else if (
							Properties[i].type() == Property.PropertyType.PT_MAXVERTS
							&&
							Properties[i].value() > Prope.value()
							)
					{	// ���� ������������ ��������� ������ ����������� �������������
						Properties[i].setValue(Prope.value());
					}
					else
					{	// ���� �������� ����� ���������� ��� �������
						// ���� �������� ������� ��������, ������� ������
						if (Properties[i].value() != Prope.value())
							TextToGraphic.Log("������� ������ ������������� ��������: "+Prope.type());
					}

					duplicateOrAbort = true;
				}
			}

			/* ��������� �� ����������� ������������� ������ ��������,
			 * ��������������� ���������� ������ ������ */
			if (!duplicateOrAbort && (Prope.type() == Property.PropertyType.PT_MINVERTS || Prope.type() == Property.PropertyType.PT_MAXVERTS || Prope.type() == Property.PropertyType.PT_HASVERTS))
			{
				for (int i = 0; i < PropCount; i++)
				{
					if (Properties[i].type() == Property.PropertyType.PT_HASVERTS)
					{	// ���� ��� ���� ��������, ��������������� ������ ���������� ������
						// �� ����� �������� �������� ��������� ������
						duplicateOrAbort = true;
						break;
					}
				}

				if (!duplicateOrAbort)
				{
					if (Prope.type() == Property.PropertyType.PT_MINVERTS)
					{ // ���� ����� �������� ������������� ����������� ���������� ������
						for (int i = 0; i < PropCount; i++)
						{
							// ���� ���� �������� ��������
							if (Properties[i].type() == Property.PropertyType.PT_MAXVERTS)
							{
								// ���� �����������
								if (Properties[i].value() == Prope.value())
								{	// ���� ��� ����������� ������� ���� �����
									// �������� ���������� �������� �� �����
									Properties[i] = new Property(Property.PropertyType.PT_HASVERTS, Prope.value());
									// ������� � ���, ��� �� ���� ������� ����� ��������
									duplicateOrAbort = true;
									break;
								}
								else if (Properties[i].value() < Prope.value())
								{	// ���� ��� ����������� �� ������� ��������
									TextToGraphic.Log("������� ������ ������������� ��������: "+Prope.type());
									// ������� � ���, ��� �� ���� ��������������� ������ ��������
									duplicateOrAbort = true;
									break;
								}
								// ����� �������� ����� ��������
							}
						}
					}
					else if (Prope.type() == Property.PropertyType.PT_MAXVERTS)
					{ // ���� ����� �������� ������������� ������������ ���������� ������
						for (int i = 0; i < PropCount; i++)
						{
							// ���� ���� �������� ��������
							if (Properties[i].type() == Property.PropertyType.PT_MINVERTS)
							{
								// ���� �����������
								if (Properties[i].value() == Prope.value())
								{	// ���� ��� ����������� ������� ���� �����
									// �������� ���������� �������� �� �����
									Properties[i] = new Property(Property.PropertyType.PT_HASVERTS, Prope.value());
									// ������� � ���, ��� �� ���� ������� ����� ��������
									duplicateOrAbort = true;
									break;
								}
								else if (Properties[i].value() > Prope.value())
								{	// ���� ��� ����������� �� ������� ��������
									TextToGraphic.Log("������� ������ ������������� ��������: "+Prope.type());
									// ������� � ���, ��� �� ���� ��������������� ������ ��������
									duplicateOrAbort = true;
									break;
								}
								// ����� �������� ����� ��������
							}
						}
					}
					else // ���� ����� �������� ������������� ������ ���������� ������
					{
						int min = -1, max = 10000;

						// ���� ����������� � ������������ ���������� ������, ���� ������� ������
						for (int i = 0; i < PropCount; i++)
						{
							if (Properties[i].type() == Property.PropertyType.PT_MINVERTS)
								min = Properties[i].value();
							else if (Properties[i].type() == Property.PropertyType.PT_MAXVERTS)
								max = Properties[i].value();
						}

						// ���� ����� �������� ������ � ���������� ��������
						if (Prope.value() >= min && Prope.value() <= max)
						{
							// ���� ��� ����� ����������� �����, ������� � �������� ���
							if (min != -1)
								for (int i = 0; i < PropCount; i++)
								{
									if (Properties[i].type() == Property.PropertyType.PT_MINVERTS)
										Properties[i] = new Property(Property.PropertyType.PT_NONE);
								}

							// ���� ��� ����� ������������ �����, ������� � �������� ���
							if (min != 10000)
								for (int i = 0; i < PropCount; i++)
								{
									if (Properties[i].type() == Property.PropertyType.PT_MINVERTS)
										Properties[i] = new Property(Property.PropertyType.PT_NONE);
								}
							// ���������� �������� ������
						}
						else // ���� �������� ������� �� ���������
						{	// �� ���������� ����� ��������
							duplicateOrAbort = true;
						}
					}
				}
			}

			// ���� �� � �������
			if (!duplicateOrAbort)
			{
				// ��������� �������� ������
				Properties[PropCount] = Prope;
				PropCount++;
			}
		}
	}

	public Property getProperty(int PropID)
	{
		return Properties[PropID];
	}

	public int propertyCount()
	{
		return PropCount;
	}

	public int fclass()
	{
		return Class;
	}
}

// �������� ������ (����������� ������)
class ProtoFigure extends VFigure
{
	// ��������
	private int Parent;

	// �������������
	ProtoFigure(int Class, int Parent)
	{
		this.Class = Class;
		this.Parent = Parent;
		PropCount = 0;
	}

	public int parent()
	{
		return Parent;
	}
	
	void addPropertiesFromAnotherPF(ProtoFigure Proto)
	{
		for (int i = 0; i < Proto.propertyCount(); i++)
		{
			addProperty(Proto.getProperty(i));
		}
	}
}

// �������� ������
class Figure extends VFigure
{
	// �������������
	Figure(int Class)
	{
		this.Class = Class;
		PropCount = 0;
	}

	// ������������� �� ������ ���������
	Figure(ProtoFigure Prototype)
	{
		this.Class = Prototype.fclass();
		PropCount = 0;
		for (int i = 0; i < Prototype.propertyCount(); i++)
			addProperty(new Property(Prototype.getProperty(i).type(), Prototype.getProperty(i).value()));
	}

	public int size()
	{
		for (int i = 0; i < PropCount; i++)
		{
			if (Properties[i].type() == Property.PropertyType.PT_SIZE)
				return Properties[i].value();
		}

		// ���� �� ���������� ������, ������ "�������"
		return 1;
	}

	// ������ ����� ������ �� ����� ����-������������������, ���� ��� ��������
	public boolean refreshClass()
	{
		// ��� ������� ��������� ������
		for (int i = 0; i < TextToGraphic.FCount; i++)
		{
			// ���� ������ �������� ����������� ������ ������
			if (TextToGraphic.ProtoFigures[i].parent() == Class)
			{
				boolean hasErrors = false;

				// ------------------------------------ToDo: ��� ���� ��������� �������

				// ������������ ��� ��������
				for (int j = 0; j < TextToGraphic.ProtoFigures[i].propertyCount(); j++)
				{
					// ���� �������� �� ������ � �� ��������� � ���������� ������
					if (TextToGraphic.ProtoFigures[i].Properties[j].type() != Property.PropertyType.PT_NONE && TextToGraphic.ProtoFigures[i].Properties[j].type() != Property.PropertyType.PT_HASVERTS && TextToGraphic.ProtoFigures[i].Properties[j].type() != Property.PropertyType.PT_MINVERTS && TextToGraphic.ProtoFigures[i].Properties[j].type() != Property.PropertyType.PT_MAXVERTS)
					{
						boolean propertyAccepted = false;

						// ���� ��� ����� �������� ����������� ��������������� �������� ������
						Property.PropertyType localType = TextToGraphic.ProtoFigures[i].Properties[j].type();
						for (int k = 0; k < PropCount; k++)
						{
							// ���� ����� ����������� ��������
							if (Properties[k].type() == localType)
							{
								// � �������� ���������
								if (TextToGraphic.ProtoFigures[i].Properties[j].value() == Properties[k].value())
								{
									// ������� � ���, ��� ����� �������� � ������� �� �����
									propertyAccepted = true;
									break;
								}
								else // ���� �������� ��������
								{
									// ������ �� ��������
									hasErrors = true;
									break;
								}
							}
						}

						if (!propertyAccepted)
						{	// ���� �������� �� ��� ������ ������
							// ������ ��� �� ��������
							hasErrors = true;
							break;
						}

						if (hasErrors)
						{	// ���� �������� ��� ������ �������������� ������
							// ��� ������ ��� �� ��������
							break;
						}
					}
				}

				// ���� ������ ��� ��������
				if (!hasErrors)
				{
					// ���������� � ����� � �������
					Class = TextToGraphic.ProtoFigures[i].fclass();

					// --------------------------------------- ToDo: ��� ������� �� ������� ��������

					return true;
				}
			}
		}
		return false;
	}
}

// ������ �����
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
			drawFigure(g, Figures.getFigure(i).fclass(), Figures.getFigure(i).size(), xPos * Wid + ImageX, yPos * Wid + ImageY, (int)Math.round(Wid * 0.9));
		}
	}

	void drawFigure(Graphics g, int figure, int size, int xPos, int yPos, int Wid)
	{
		// ������������ 0 - ���������, 1 - ����������, 2 �������
		Wid = (int)Math.round(Wid * (size + 1) / 3.0);

		// ������ ��������� ������
		switch (figure)
		{
			case 0: // ������
			case 1: // ����
				g.drawOval(xPos, yPos + Wid / 4, Wid, Wid / 2);
				break;
			case 2: // ������
				g.drawOval(xPos, yPos + Wid / 4, Wid, Wid / 2);
				break;
			case 3: // ����
				g.drawOval(xPos, yPos, Wid, Wid);
				break;
			case 4: // �������
			case 5: // 4-��������
			case 6: // �������������
				g.drawRect(xPos, yPos + Wid / 4, Wid, Wid / 2);
				break;
			case 7: // �������
				g.drawRect(xPos, yPos, Wid, Wid);
				break;
			case 8: // �����������
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
	static String FNames[][];
	static ProtoFigure ProtoFigures[];
	static int FCount = 0;

	// --------------- ��������� ------------------
	// �������� ��������
	// ����-��������
	final static String CLOSED_NAME = "���������|��������|���������";
	final static String UNCLOSED_NAME = "����������|�����������|�� ���������|�� ��������|�� ���������|���������|�������|���������";
	final static String SYMMETRIC_NAME = "������������|�����������|������������|������������";
	final static String UNSYMMETRIC_NAME = "��c�����������|�� �����������|��c������������|�� ������������";
	// ����������
	final static String HASPART_NAME = "����������|����������|����������";
	final static String HAS_NAME = "�������|�������|�������";

	// ���������� ��������
	final static String BIG_NAME = "�������|�������";
	final static String SMALL_NAME = "���������|���������";

	// ----- ��������� ���������
	// �����-���� ������
	static String SOME_FIGURE = ""; // ������������
	static String SOME_PFIGURE = ""; // ����������

	// �����-���� ����-��������
	final static String SOME_PREPROPERTY = CLOSED_NAME+"|"+UNCLOSED_NAME+"|"+UNSYMMETRIC_NAME+"|"+SYMMETRIC_NAME+"|"+BIG_NAME+"|"+SMALL_NAME;

	// ----- ���������� ���������
	// ���������� ������
	final static String WORD_CHAR = "[�-��-�a-zA-Z_0-9]";

	// ����� � �����
	final static String HAS_NVERTS = "("+HAS_NAME+")[\\s]+[0-9]+(����|����|�����|�������|�������|������)([\\s]|\\.|$)";

	// ������ � ������
	static String FIGURE_IN_FIGURE;

	// �����������
	final static String PROPOSITION = WORD_CHAR+"[^.]*(\\.|$)";

	// ����� ���������� �������������� ����������
	static String ANY_FIGURE;

	// ����-��������
	final static String ANY_PREPROPERTY = "(^|[\\s])+("+SOME_PREPROPERTY+")([\\s]|\\.|,|$)+";
	final static String ANY_PREPROPERTY2 = "(^|[\\s])*("+SOME_PREPROPERTY+")([\\s]|\\.|,|$)+";

	// ����-�������� � �������� ������
	static String PROPERTY_FIGURE;

	// ����������� ��� ������� ����������
	public static void main(String[] args) throws ClassNotFoundException
	{
		Log("�������� � ����� ������");

		createNewDataBase();
		readDataBase();

		Log("�������� ����");
		// ������ ����� ���� � ��������� ���������
		Window1 f = new Window1("�������");
		f.setSize(1000, 500);
		f.setVisible(true);
	}

	// ������ ����������� ������ � ��
	public static void createNewDataBase() throws ClassNotFoundException
	{
		// ���������� ������� ��� ������ � SQLite
		Class.forName("org.sqlite.JDBC");

		Connection connection = null;
		try
		{
			// ������ ����������� � ��
			connection = DriverManager.getConnection("jdbc:sqlite:onto.db");
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);

			// ���������� ������� FIGURES, ���� ����� ����
			statement.executeUpdate("drop table if exists FIGURES");
			// ������ �������
			statement.executeUpdate("create table FIGURES (ID_FIG integer, PARENT integer, INAME string, RNAME string, DNAME string, VNAME string, TNAME string, PNAME string)");
			// ��������� ������
			statement.executeUpdate("insert into FIGURES values(0, -1, '������', '������', '������', '������', '�������', '������')");
			statement.executeUpdate("insert into FIGURES values(1, 0,'����', '�����', '�����', '����', '������', '�����')");
			statement.executeUpdate("insert into FIGURES values(2, 1,'������', '�������', '�������', '������', '��������', '�������')");
			statement.executeUpdate("insert into FIGURES values(3, 2,'����', '�����', '�����', '����', '������', '�����')");
			statement.executeUpdate("insert into FIGURES values(4, 0,'�������', '��������', '��������', '�������', '���������', '��������')");
			statement.executeUpdate("insert into FIGURES values(5, 4,'��������������', '���������������', '���������������', '��������������', '����������������', '���������������')");
			statement.executeUpdate("insert into FIGURES values(6, 5,'�������������', '��������������', '��������������', '�������������', '���������������', '��������������')");
			statement.executeUpdate("insert into FIGURES values(7, 6,'�������', '��������', '��������', '�������', '���������', '��������')");
			statement.executeUpdate("insert into FIGURES values(8, 4,'�����������', '������������', '������������', '�����������', '�������������', '������������')");
			statement.executeUpdate("insert into FIGURES values(9, 4,'�������������', '��������������', '��������������', '�������������', '���������������', '��������������')");

			// ���������� ������� PROPERTIES, ���� ����� ����
			statement.executeUpdate("drop table if exists PROPERTIES");
			// ������ �������
			statement.executeUpdate("create table PROPERTIES (ID_PROP integer, ID_FIG integer, PROPERTY integer, VALUE integer)");
			// ��������� ������
			statement.executeUpdate("insert into PROPERTIES values(0, 1, 2, 1)");
			statement.executeUpdate("insert into PROPERTIES values(1, 1, 3, 0)");
			statement.executeUpdate("insert into PROPERTIES values(2, 2, 1, 1)");
			statement.executeUpdate("insert into PROPERTIES values(3, 3, 1, 2)");
			statement.executeUpdate("insert into PROPERTIES values(4, 4, 2, 1)");
			statement.executeUpdate("insert into PROPERTIES values(5, 4, 4, 3)");
			statement.executeUpdate("insert into PROPERTIES values(6, 5, 3, 4)");
			statement.executeUpdate("insert into PROPERTIES values(7, 6, 1, 1)");
			statement.executeUpdate("insert into PROPERTIES values(8, 7, 1, 2)");
			statement.executeUpdate("insert into PROPERTIES values(9, 8, 3, 3)");
			statement.executeUpdate("insert into PROPERTIES values(10, 9, 4, 5)");
			// ����
			//statement.executeUpdate("insert into PROPERTIES values(11, 3, 0, 2)");
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
		}
		finally
		{
			try
			{
				if(connection != null)
				connection.close();
			}
			catch(SQLException e)
			{
				// ���� �� ������� ������� ����������� � ��
				System.err.println(e);
			}
		}
	}

	// ��������� ������ �� ��
	public static void readDataBase() throws ClassNotFoundException
	{
		// ���������� ������� ��� ������ � SQLite
		Class.forName("org.sqlite.JDBC");

		Connection connection = null;
		try
		{
			// ������ ����������� � ��
			connection = DriverManager.getConnection("jdbc:sqlite:onto.db");
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);

			// ����� ���������� ����� � ��
			int count = 0;
			ResultSet rs = statement.executeQuery("select count(*) as COUNT from FIGURES");
			while(rs.next())
			{
				count = rs.getInt("COUNT");
			}

			// �������� ������ ��� ������
			FNames = new String[count][6];
			ProtoFigures = new ProtoFigure[count];

			// �������� ��� ���������� � ������� �� ������� FIGURES
			int id;
			rs = statement.executeQuery("select * from FIGURES");
			while(rs.next())
			{
				id = rs.getInt("ID_FIG");
				FNames[id][0] = rs.getString("INAME");
				FNames[id][1] = rs.getString("RNAME");
				FNames[id][2] = rs.getString("DNAME");
				FNames[id][3] = rs.getString("VNAME");
				FNames[id][4] = rs.getString("TNAME");
				FNames[id][5] = rs.getString("PNAME");

				SOME_FIGURE += (id > 0 ? "|" : "") + FNames[id][0];
				SOME_PFIGURE += (id > 0 ? "|" : "") + FNames[id][5];
				ProtoFigures[id] = new ProtoFigure(id, rs.getInt("PARENT"));

				Log("��������� ������: " + FNames[id][0]);

				FCount++;
			}

			// �������� ��� �������� �����
			rs = statement.executeQuery("select ID_FIG, PROPERTY, VALUE from PROPERTIES");
			while(rs.next())
			{
				ProtoFigures[rs.getInt("ID_FIG")].addProperty(new Property(rs.getInt("PROPERTY"), rs.getInt("VALUE")));
			}
			
			// ��������� ��� ������ ����������� �������� ���� � ���������
			for (int i = 0; i < FCount; i++)
			{
				// ���� ��� �� ������� �����
				if (ProtoFigures[i].parent() != -1)
				{	// ��������� �������� �� �����������
					ProtoFigures[i].addPropertiesFromAnotherPF(ProtoFigures[ProtoFigures[i].parent()]);
				}
			}

			// ����� ���������� ���������, ��������� �� ������ �� ��
			ANY_FIGURE = "(^|[\\s])+("+SOME_FIGURE+")([\\s]|\\.|,|$)";
			FIGURE_IN_FIGURE = "("+SOME_FIGURE+")[\\s]+(�)[\\s]+("+SOME_PFIGURE+")([\\s]|\\.|$)";
			PROPERTY_FIGURE = "[\\s]*(("+SOME_PREPROPERTY+")[^.]*)+[\\s]+("+SOME_FIGURE+")([\\s]|\\.|,|$)";
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
		}
		finally
		{
			try
			{
				if(connection != null)
				connection.close();
			}
			catch(SQLException e)
			{
				// ���� �� ������� ������� ����������� � ��
				System.err.println(e);
			}
		}
	}

	// ����� ���� � ��������� ������� � �������
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

				String Next_String = Propn;
				String This_String;
				String FigureName;
				String PropertyName;

				// ��� ������ ������
				for (int j = 0; j < getCountOfStringsLikeThis(Propn, ANY_FIGURE); j++)
				{
					This_String = getStartStringLikeThis(Next_String, ANY_FIGURE);
					Next_String = getEndStringLikeThis(Next_String, ANY_FIGURE);

					// ������ ������ � ������� � ��������
					FigureName = getStringLikeThis(getStringLikeThis(Propn, ANY_FIGURE, j), SOME_FIGURE);
					Figure thisFigure = new Figure(ProtoFigures[getFigureID(FigureName)]);
					ta.append("���������� ������: "+FigureName+"\n");

					// ���� ���������� ����-��������
					if (hasStringLikeThis(This_String, PROPERTY_FIGURE))
					{
						// ������� ��� ����-��������
						String PropertiesString = "�������� ������ \""+FigureName+"\": ";
						for (int k = 0; k < getCountOfStringsLikeThis(This_String, ANY_PREPROPERTY2); k++)
						{
							// ������� ���, ��������� �� ��������
							PropertyName = getStringLikeThis(getStringLikeThis(This_String, ANY_PREPROPERTY2, k), SOME_PREPROPERTY);
							PropertiesString += PropertyName + " ";
							// ��������� ������ ��������
							thisFigure.addProperty(getPropertyByName(PropertyName));
						}
						ta.append(PropertiesString+"\n");
					}

					// ���������� ����� ����� ������
					if (thisFigure.refreshClass())
					{	// ���� ������ �������� �����, �� �������� �� ����
						ta.append("��� �������� ��������: "+FNames[thisFigure.fclass()][0]+"\n");
					}
					// ��������� ������ � ������ �� ���������
					fr.insertFigure(thisFigure);
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
		int count = 0;
		Pattern p = Pattern.compile(mask, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(st.toLowerCase());
		while (m.find())
		{
			count++;
		}
		return count;
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
		int count = 0;
		Pattern p = Pattern.compile(mask, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(st.toLowerCase());
		while (m.find())
		{
			if (count == ordNumber)
				return m.group();

			count++;
		}
		return "";
	}

	// ������ ������ ������� ������ ��������� n-���� ���������, ���������������� �������
	private static int getStartPosStringLikeThis(String st, String mask, int ordNumber)
	{
		int count = 0;
		Pattern p = Pattern.compile(mask, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(st.toLowerCase());
		while (m.find())
		{
			if (count == ordNumber)
			{
				return m.start();
			}

			count++;
		}
		return -1;
	}

	// ������ ������ ������� ����� ��������� n-���� ���������, ���������������� �������
	private static int getEndPosStringLikeThis(String st, String mask, int ordNumber)
	{
		int count = 0;
		Pattern p = Pattern.compile(mask, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(st.toLowerCase());
		while (m.find())
		{
			if (count == ordNumber)
			{
				return m.end();
			}

			count++;
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

	// ������� ������, ������� ��������� ����� ������� ���������, ���������������� �������
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

	// (�������) ������� ������, ������� ��������� ����� n-���� ���������, ���������������� �������
	private static String getEndStringLikeThis(String st, String mask, int ordNumber)
	{
		int end = getEndPosStringLikeThis(st, mask, ordNumber);
		if (end != -1)
			return st.substring(end, st.length());
		else
			return st;
	}

	// ���������� ID ������
	private static int getFigureID(String FigureName)
	{
		for (int i  = 0; i < FCount; i++)
		{
			// ���� �������� ������ � ������������ ��� ���������� �������
			if (hasStringLikeThis(FigureName, FNames[i][0]+"|"+FNames[i][3]))
				return i;
		}
		return -1;
	}

	// ���������� �������� �� ��������
	private static Property getPropertyByName(String PropertyName)
	{
		Property Proper;

		if (hasStringLikeThis(PropertyName, BIG_NAME))
		{
			Proper = new Property(Property.PropertyType.PT_SIZE, 2);
		}
		else if (hasStringLikeThis(PropertyName, SMALL_NAME))
		{
			Proper = new Property(Property.PropertyType.PT_SIZE, 0);
		}
		else if (hasStringLikeThis(PropertyName, SYMMETRIC_NAME))
		{
			Proper = new Property(Property.PropertyType.PT_SYMMETRIC, 1);
		}
		else if (hasStringLikeThis(PropertyName, CLOSED_NAME))
		{
			Proper = new Property(Property.PropertyType.PT_CLOSED, 1);
		}
		else if (hasStringLikeThis(PropertyName, UNCLOSED_NAME))
		{
			Proper = new Property(Property.PropertyType.PT_CLOSED, 0);
		}
		else if (hasStringLikeThis(PropertyName, UNSYMMETRIC_NAME))
		{
			Proper = new Property(Property.PropertyType.PT_SYMMETRIC, 0);
		}
		else
		{
			// ���� �������� �� �������, ������ ��������-��������
			Proper = new Property();
		}

		return Proper;
	}
}

// ���������� �������
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
		window.update(window.getGraphics());
		window.paint(window.getGraphics());
	}
}