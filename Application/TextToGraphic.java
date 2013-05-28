import java.awt.*;
import java.awt.event.*;
import java.sql.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

/*
 *	В коде использована прямая конкатенация строк, что является неоптимальным решением.
 *	Если, в будущем, приложение планируется использовать для обработки больших текстов,
 *	то стоит использовать StringBuffer для сложения строк.
 */

// свойство
class Property
{
	/* Type
	 *	0 // 0 - маленький, 1 - средний, 2 - большой
	 *	1 // 0 - не симметричен, 1 - симетричен относительно прямой, 2 - симметричен относительно двух перпендикулярных прямых
	 *	2 // 0 - разомкнут, 1 - замкнут
	 *	3 // точное количество вершин
	 *	4 // имеет как минимум это количество вершин
	 *	5 // имеет максимум это количество вершин
	 *	6 // пустое свойство (всегда должно остоваться последним)
	 */

	// тип свойства
	private int Type;
	// значение свойства
	private int Value = -1;

	// последнее стандартное свойство (пустое)
	final static public int EMPTY_PROPERTY = 6;

	// инициализация c числовым значением типа свойства
	Property(int Type)
	{
		this.Type = Type;
	}

	// инициализация c числовым значением типа свойства и со значением свойства
	Property(int Type, int Value)
	{
		this.Type = Type;
		this.Value = Value;
	}

	// инициализация свойства-пустышки
	Property()
	{
		Type = EMPTY_PROPERTY;
	}

	public void setValue(int Value)
	{
		this.Value = Value;
	}

	// получить тип свойства
	public int type()
	{
		return Type;
	}

	public int value()
	{
		return Value;
	}
}

// виртуальный класс, родитель фигуры и протофигуры
class VFigure
{
	// что за фигура
	protected int Class;

	// массив свойств
	protected Property[] Properties = new Property[10];
	protected int PropCount;

	// добавить новое свойство фигуры
	public void addProperty(Property Prope)
	{
		// если свойство и значение свойства заданы
		if (Prope.type() != Property.EMPTY_PROPERTY && Prope.value() != -1)
		{
			boolean duplicateOrAbort = false;

			// ищем, не было ли уже такого свойства
			for (int i = 0; i < PropCount; i++)
			{
				// если такое свойство есть
				if (Properties[i].type() == Prope.type())
				{
					if (
						Properties[i].type() == 1
						&&
						Properties[i].value() == 1
						&&
						Prope.value() > 0
						)
					{	// если фигура была симметрична относительно прямой
						// добавляем симметричность относительно двух перепендикулярных прямых
						Properties[i].setValue(2);
					}
					else if (
							Properties[i].type() == 4
							&&
							Properties[i].value() < Prope.value()
							)
					{	// если минимальное количесво больше предыдущего минимального
						Properties[i].setValue(Prope.value());
					}
					else if (
							Properties[i].type() == 5
							&&
							Properties[i].value() > Prope.value()
							)
					{	// если максимальное количесво меньше предыдущего максимального
						Properties[i].setValue(Prope.value());
					}

					// в любом случае, это свойсто не нужно добавлять
					duplicateOrAbort = true;
				}
			}

			/* проверяем на возможность существования нового свойства,
			 * ограничивающего количество вершин фигуры */
			if (!duplicateOrAbort && (Prope.type() == 4 || Prope.type() == 5 || Prope.type() == 3))
			{
				for (int i = 0; i < PropCount; i++)
				{
					if (Properties[i].type() == 3)
					{	// если уже есть свойство, устанавливающее точное количество вершин,
						// то новые подобные свойства добавлять нельзя
						duplicateOrAbort = true;
						break;
					}
				}

				if (!duplicateOrAbort)
				{
					if (Prope.type() == 4)
					{ // если новое свойство устанавливает минимальное количество вершин
						for (int i = 0; i < PropCount; i++)
						{
							// если есть обратное свойство
							if (Properties[i].type() == 5)
							{
								// ищем пересечение
								if (Properties[i].value() == Prope.value())
								{	// если при пересечение остаётся одно число
									// заменяем предыдущее свойство на новое
									Properties[i] = new Property(3, Prope.value());
									// говорим о том, что не надо вносить новое свойство
									duplicateOrAbort = true;
									break;
								}
								else if (Properties[i].value() < Prope.value())
								{	// если при пересечении не остаётся значений
									// говорим о том, что не надо проигнорировать данное свойство
									duplicateOrAbort = true;
									break;
								}
								// иначе свойство можно добавить
							}
						}
					}
					else if (Prope.type() == 5)
					{ // если новое свойство устанавливает максимальное количество вершин
						for (int i = 0; i < PropCount; i++)
						{
							// если есть обратное свойство
							if (Properties[i].type() == 4)
							{
								// ищем пересечение
								if (Properties[i].value() == Prope.value())
								{	// если при пересечение остаётся одно число
									// заменяем предыдущее свойство на новое
									Properties[i] = new Property(3, Prope.value());
									// говорим о том, что не надо вносить новое свойство
									duplicateOrAbort = true;
									break;
								}
								else if (Properties[i].value() > Prope.value())
								{	// если при пересечении не остаётся значений
									// говорим о том, что не надо проигнорировать данное свойство
									duplicateOrAbort = true;
									break;
								}
								// иначе свойство можно добавить
							}
						}
					}
					else // если новое свойство устанавливает точное количество вершин
					{
						int min = -1, max = 10000;

						// ищем минимальное и максимальное количество вершин, если таковые заданы
						for (int i = 0; i < PropCount; i++)
						{
							if (Properties[i].type() == 4)
								min = Properties[i].value();
							else if (Properties[i].type() == 5)
								max = Properties[i].value();
						}

						// если новое значение входит в допустимый диапазон
						if (Prope.value() >= min && Prope.value() <= max)
						{
							// если был задан минимальный порог, находим и обнуляем его
							if (min != -1)
								for (int i = 0; i < PropCount; i++)
								{
									if (Properties[i].type() == 4)
										Properties[i] = new Property();
								}

							// если был задан максимальный порог, находим и обнуляем его
							if (min != 10000)
								for (int i = 0; i < PropCount; i++)
								{
									if (Properties[i].type() == 4)
										Properties[i] = new Property();
								}
							// пропускаем свойство дальше
						}
						else // если значение выходит из диапазона
						{	// не пропускаем новое свойство
							duplicateOrAbort = true;
						}
					}
				}
			}

			// если всё в порядке
			if (!duplicateOrAbort)
			{
				// добавляем свойство фигуре
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

// прототип фигуры (виртуальная фигура)
class ProtoFigure extends VFigure
{
	// родитель
	private int Parent;

	// инициализация
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

	// добавляем все свойства другого прототипа
	void addPropertiesFromAnotherPF(ProtoFigure Proto)
	{
		for (int i = 0; i < Proto.propertyCount(); i++)
		{
			addProperty(Proto.getProperty(i));
		}
	}
}

// реальная фигура
class Figure extends VFigure
{
	// инициализация
	Figure(int Class)
	{
		this.Class = Class;
		PropCount = 0;
	}

	// инициализация другой фигурой
	Figure(Figure fig)
	{
		Class = fig.Class;
		PropCount = 0;
		for (int i = 0; i < fig.propertyCount(); i++)
			addProperty(new Property(fig.getProperty(i).type(), fig.getProperty(i).value()));
	}

	// инициализация на основе прототипа
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
			if (Properties[i].type() == 0)
				return Properties[i].value();
		}

		// если не установлен размер, ставим "средний"
		return 1;
	}

	// меняем класс фигуры на более узко-специализированный, если это возможно
	public boolean refreshClass()
	{
		// для каждого прототипа фигуры
		for (int i = 0; i < TextToGraphic.FCount; i++)
		{
			// если фигура является наследником данной фигуры
			if (TextToGraphic.ProtoFigures[i].parent() == Class)
			{
				boolean hasErrors = false;

				// ищем минимальное и максимальное количество вершин протофигуры
				int MinVertsPF = -1, MaxVertsPF = 10000;
				for (int j = 0; j < TextToGraphic.ProtoFigures[i].propertyCount(); j++)
				{
					if (TextToGraphic.ProtoFigures[i].Properties[j].type() == 3)
					{	// если устанавливается точное количество вершин
						// устанавливаем максимальное и минимальное количество вершин
						MinVertsPF = TextToGraphic.ProtoFigures[i].Properties[j].value();
						MaxVertsPF = MinVertsPF;
						// дальше нет смысла что-то искать
						break;
					}
					else if (TextToGraphic.ProtoFigures[i].Properties[j].type() == 4)
					{
						// устанавливаем минимальное количество вершин
						MinVertsPF = TextToGraphic.ProtoFigures[i].Properties[j].value();
					}
					else if (TextToGraphic.ProtoFigures[i].Properties[j].type() == 5)
					{
						// устанавливаем максимальное количество вершин
						MaxVertsPF = TextToGraphic.ProtoFigures[i].Properties[j].value();
					}
				}

				// ищем минимальное и максимальное количество вершин нашей фигуры
				int MinVertsOF = -1, MaxVertsOF = 10000;
				for (int j = 0; j < PropCount; j++)
				{
					if (Properties[j].type() == 3)
					{	// если устанавливается точное количество вершин
						MinVertsOF = Properties[j].value();
						MaxVertsOF = MinVertsOF;
						// дальше нет смысла что-то искать
						break;
					}
					else if (Properties[j].type() == 4)
					{
						// устанавливаем максимальное количество вершин
						MinVertsOF = Properties[j].value();
					}
					else if (Properties[j].type() == 5)
					{
						// устанавливаем минимальное количество вершин
						MaxVertsOF = Properties[j].value();
					}
				}

				// если количество углов не подходит
				if (MinVertsOF != MinVertsPF || MaxVertsOF != MaxVertsPF)
				{
					hasErrors = true;
				}

				// если нет несоответствий
				if (!hasErrors)
				{
					// сопоставляем все свойства
					for (int j = 0; j < TextToGraphic.ProtoFigures[i].propertyCount(); j++)
					{
						// если свойство не пустое и не относится к количеству вершин
						if (TextToGraphic.ProtoFigures[i].Properties[j].type() != Property.EMPTY_PROPERTY && (TextToGraphic.ProtoFigures[i].Properties[j].type() < 3 || TextToGraphic.ProtoFigures[i].Properties[j].type() > 5))
						{
							boolean propertyAccepted = false;

							// ищем для этого совйства протофигуры соответствующее свойство фигуры
							int localType = TextToGraphic.ProtoFigures[i].Properties[j].type();
							for (int k = 0; k < PropCount; k++)
							{
								// если нашли совподающее свойство
								if (Properties[k].type() == localType)
								{
									// если свойство относится к симметричности
									if (localType == 1)
									{
										if (TextToGraphic.ProtoFigures[i].Properties[j].value() == Properties[k].value() || (TextToGraphic.ProtoFigures[i].Properties[j].value() == 1 && Properties[k].value() == 2))
										{	// если симметричность не противоречива
											// говорим о том, что нашли свойство и выходим из цикла
											propertyAccepted = true;
											break;
										}
									}
									else // если свойство не относится к симметричности
									{
										if (TextToGraphic.ProtoFigures[i].Properties[j].value() == Properties[k].value())
										{	// если значение свойств совпадает
											// говорим о том, что нашли свойство и выходим из цикла
											propertyAccepted = true;
											break;
										}
										else // если значения разнятся
										{
											// фигура не подходит
											hasErrors = true;
											break;
										}
									}
								}
							}

							if (!propertyAccepted)
							{	// если свойству не был найден аналог
								// фигура нам не подходит
								hasErrors = true;
								break;
							}

							if (hasErrors)
							{	// если свойству был найден противоречивый аналог
								// эта фигура нам не подходит
								break;
							}
						}
					}
				}

				// если фигура нам подходит
				if (!hasErrors)
				{
					// используем её класс
					Class = TextToGraphic.ProtoFigures[i].fclass();

					// пробуем найти подкласс для новой фигуры
					refreshClass();

					return true;
				}
			}
		}
		return false;
	}

	// отрисовываем данную фигуру
	public void draw(Graphics g, int xPos, int yPos, int Wid)
	{
		// рисуем выбранную фигуру
		switch (Class)
		{
			case 0: // фигура
				break;
			case 1: // овал
				g.drawOval(xPos - Wid/2, yPos - Wid/2 + Wid / 4, Wid, Wid / 2);
				break;
			case 2: // эллипс
				g.drawOval(xPos - Wid/2, yPos - Wid/2 + Wid / 4, Wid, Wid / 2);
				break;
			case 3: // круг
				g.drawOval(xPos - Wid/2, yPos - Wid/2, Wid, Wid);
				break;
			case 4: // полигон
				break;
			case 5: // 4-угольник
				break;
			case 6: // прямоугольник
				g.drawRect(xPos - Wid/2, yPos - Wid/2 + Wid / 4, Wid, Wid / 2);
				break;
			case 7: // квадрат
				g.drawRect(xPos - Wid/2, yPos - Wid/2, Wid, Wid);
				break;
			case 8: // треугольник
				int[] arrX = {xPos - Wid / 2, xPos + Wid / 2, xPos};
				int[] arrY = {yPos + Wid / 2, yPos + Wid / 2, yPos - Wid / 2};
				g.drawPolygon(arrX, arrY, 3);
				break;
			case 9: // многоугольник
				break;
			default:
				break;
		}
	}
}

// коллекция из фигур
class FiguresCollection
{
		// две фигуры
	public Figure FirstFigure, SecondFigure;

	/* тип связи
	 *	0 - одиночная фигура
	 *	1 - фигура 1 в фигуре 2
	 *	2 - фигуры пересекаются
	 *	3 - фигура 1 над фигурой 2
	 *	4 - фигура 1 под фигурой 2
	 *	5 - фигура 1 слева от фигуры 2
	 *	6 - фигура 1 справа от фигуры 2
	 */
	int ConnectionType;

	// инициируем
	FiguresCollection(Figure fig1, Figure fig2, int type)
	{
		FirstFigure = fig1;
		SecondFigure = fig2;
		ConnectionType = type;
	}

	// инициируем другой коллекцией (по значению)
	FiguresCollection(FiguresCollection FC)
	{
		FirstFigure = new Figure(FC.FirstFigure);
		SecondFigure = new Figure(FC.SecondFigure);
		ConnectionType = FC.ConnectionType;
	}

	// инициируем одиночной фигурой
	FiguresCollection(Figure fig)
	{
		FirstFigure = new Figure(fig);
		ConnectionType = 0;
	}

	// добавляем вторую фигуру
	public void addSecondFigure(Figure fig)
	{
		SecondFigure = new Figure(fig);
	}

	public void setType(int type)
	{
		ConnectionType = type;
	}

	public void draw(Graphics g, int xPos, int yPos, int Wid)
	{
		// масштабируем 0 - маленький, 1 - нормальный, 2 большой
		int FstWid = (int)Math.round(Wid * (FirstFigure.size() + 1) / 3.0);
		int SndWid = 0;
		int conType = 0;
		if (SecondFigure != null)
		{
			SndWid = (int)Math.round(Wid * (SecondFigure.size() + 1) / 3.0);
			conType = ConnectionType;
		}

		switch (conType)
		{
			case 0:
				FirstFigure.draw(g, xPos, yPos, FstWid);
				break;
			case 1:
				FirstFigure.draw(g, xPos, yPos, FstWid/3);
				SecondFigure.draw(g, xPos, yPos, SndWid);
				break;
			case 2:
				FirstFigure.draw(g, xPos-FstWid / 10, yPos-FstWid / 12, FstWid);
				SecondFigure.draw(g, xPos+FstWid / 10, yPos+FstWid / 12, SndWid);
				break;
			case 3:
				FirstFigure.draw(g, xPos, yPos - FstWid/4, FstWid/3 + FstWid/7);
				SecondFigure.draw(g, xPos, yPos + FstWid/4, SndWid/3 + SndWid/7);
				break;
			case 4:
				FirstFigure.draw(g, xPos, yPos + FstWid/4, FstWid/3 + FstWid/7);
				SecondFigure.draw(g, xPos, yPos - FstWid/4, SndWid/3 + SndWid/7);
				break;
			case 5:
				FirstFigure.draw(g, xPos - FstWid/4, yPos, FstWid/3 + FstWid/7);
				SecondFigure.draw(g, xPos + FstWid/4, yPos, SndWid/3 + SndWid/7);
				break;
			case 6:
				FirstFigure.draw(g, xPos + FstWid/4, yPos, FstWid/3 + FstWid/7);
				SecondFigure.draw(g, xPos - FstWid/4, yPos, SndWid/3 + SndWid/7);
				break;
			default:
				FirstFigure.draw(g, xPos, yPos, FstWid);
				break;
		}
	}
}

// массив коллекций фигур
class FiguresMass
{
	private FiguresCollection[] Mass;
	private int Count;

	// инициируем массив
	FiguresMass()
	{
		Mass = new FiguresCollection[50];
		Count = 0;
	}

	// очищаем массив
	public void reset()
	{
		Count = 0;
	}

	// узнаём количество коллекций
	public int count()
	{
		return Count;
	}

	// берём коллекцию фигур
	public FiguresCollection getFigure(int pos)
	{
		return Mass[pos];
	}

	// добавляем коллекцию фигур
	public void insertCollection(FiguresCollection FC)
	{
		Mass[Count] = new FiguresCollection(FC);
		Count++;
	}

	// добавляем одиночную фигуру
	public void insertFigure(Figure fig)
	{
		Mass[Count] = new FiguresCollection(fig);
		Count++;
	}

	// отрисовываем массив объектов
	public void draw(Graphics g, int ImageX, int ImageY, int ImageSize)
	{
		int xPos = 0, yPos = 0, xSize = 1, ySize = 0, Wid = 0;

		boolean isFinish = false;
		while (!isFinish)
		{
			if (Count <= xSize * xSize)
			{
				ySize = Count / xSize + 1;
				Wid = ImageSize/xSize;
				isFinish = true;
			}
			else
			{
				xSize++;
			}
		}

		for (int i = 0; i < Count; i++)
		{
			xPos = i%xSize;
			yPos = i/xSize;
			Mass[i].draw(g, xPos * Wid + Wid / 2 + ImageX, yPos * Wid + Wid / 2 + ImageY, (int)Math.round(Wid * 0.9));
		}
	}
}

// наше окно
class Window1 extends Frame
{
	// массив фигур
	FiguresMass Figures = new FiguresMass();

	// объекты окна
	TextArea Field;
	Label Label1;
	TextArea Area;
	MenuBar MenuB = new MenuBar();
	Checkbox CB;

	// инициализация
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
		Label1.setBounds(500, 410, 900, 30);
		add(Label1);

		// поле вывода
		Area = new TextArea("", 50, 50, TextArea.SCROLLBARS_BOTH);
		Area.setEditable(false);
		Area.setBounds(500, 100, 450, 250);
		add(Area);

		// создание поля ввода
		Field = new TextArea("", 50, 50, TextArea.SCROLLBARS_VERTICAL_ONLY);
		//Field.setEditable(false);
		Field.setBounds(25, 360, 450, 70);
		add(Field);

		// создание кнопки 1
		Button Btn1 = new Button("Распознать");
		// размещение кнопки
		Btn1.setBounds(500, 380, 100, 30);
		add(Btn1);

		// создание кнопки 2
		Button Btn2 = new Button("Очистить");
		// размещение кнопки
		Btn2.setBounds(630, 380, 100, 30);
		add(Btn2);

		CB = new Checkbox("Распознавание в реальном времени", true);
		CB.setBounds(500, 350, 300, 30);
		add(CB);

		// обработчик ввода текста
		Field.addTextListener(new ActLis(this));

		// нажатие кнопки "Распознать"
		Btn1.addActionListener(new ActLis(this));

		// нажатие кнопки "Очистить"
		Btn2.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{	// очищаем поле ввода
				Field.replaceRange("", 0, 10000);
			}
		});

		// главное меню приложения
		setMenuBar(MenuB);

		Menu MFile = new Menu("Файл");
		Menu MHelp = new Menu("Справка");

		MenuB.add(MFile);
		MenuB.add(MHelp);

		MenuItem MIOpen = new MenuItem("Открыть файл", new MenuShortcut(KeyEvent.VK_O));
		MenuItem MISave = new MenuItem("Сохранить текст..", new MenuShortcut(KeyEvent.VK_S));
		MenuItem MIExit = new MenuItem("Выход", new MenuShortcut(KeyEvent.VK_Q));

		MenuItem MIAbout = new MenuItem("О программе");

		MFile.add(MIOpen);
		MFile.add(MISave);
		MFile.addSeparator();
		MFile.add(MIExit);

		MHelp.add(MIAbout);

		// Открыть файл
		MIOpen.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				FileDialog fd = new FileDialog(new Frame(),	"Открыть файл", FileDialog.LOAD);
				fd.setVisible(true);
				if (fd.getFile() != null)
				{
					String FilePath = fd.getDirectory()+fd.getFile();
					readFile(FilePath);
				}
			}
		});

		// Сохранить текст
		MISave.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				FileDialog fd = new FileDialog(new Frame(),	" Сохранить как..", FileDialog.LOAD);
				fd.setVisible(true);
				if (fd.getFile() != null)
				{
					String FilePath = fd.getDirectory()+fd.getFile();
					writeToFile(FilePath);
				}
			}
		});

		// Выход
		MIExit.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				System.exit(0);
			}
		});

		// О программе
		MIAbout.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JOptionPane.showMessageDialog(null, "Распознавание текста\nстудент НИИ ИС-09 Павел Гребнев\n2013г", "О программе", JOptionPane.INFORMATION_MESSAGE);
			}
		});
	}

	// процедура перерисовки окна
	public void paint(Graphics g)
	{
		Figures.draw(g, 100, 50, 300);
	}

	// считать содержимое файла в Field
	public void readFile(String FileName)
    {
        try
        {
            FileReader fr = new FileReader(FileName);
            StringBuffer sb = new StringBuffer();
            int symbol;
            while((symbol = fr.read()) != -1)
            {
                sb.append((char)symbol);
            }
            Field.setText(sb.toString());
        }
        catch (FileNotFoundException ex)
        {
            JOptionPane.showMessageDialog(null, "Не удалось найти файл.", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
        catch (IOException ex)
        {
            JOptionPane.showMessageDialog(null, "Не удалось прочитать файл. Возможно файл недоступен для чтения или повреждён.", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

	// сохранить содержимое Field в файл
	public void writeToFile(String FileName)
    {
		String strText = Field.getText().trim();
		try
		{
			FileWriter fw = new FileWriter(FileName);
			fw.write(strText);
			fw.flush();
			fw.close();
			JOptionPane.showMessageDialog(null, "Файл сохранён.", "Успешно", JOptionPane.INFORMATION_MESSAGE);
		}
		catch (IOException ex)
		{
			JOptionPane.showMessageDialog(null, "Не удалось записать файл.\nВозможно файл или каталог защищены от записи.", "Ошибка", JOptionPane.ERROR_MESSAGE);
		}
    }
}

// основной класс
class TextToGraphic
{
	static String FNames[][];
	static String PropNames[][];
	static String RelNames[];

	static ProtoFigure ProtoFigures[];
	static int FCount = 0;
	static int PCount = 0;
	static int RCount = 0;

	// --------------- КОНСТАНТЫ (либо выражения, которые задаются один раз при старте программы) ------------------

	// ----- составные выражения
	// какая-либо фигура
	static String SOME_FIGURE = "";

	// какое-либо пред-свойство
	static String SOME_PREPROPERTY = "";

	// какое-либо отношение между фигурами
	static String SOME_RELATION = "";

	// ----- регулярные выражения
	// словестный символ
	final static String WORD_CHAR = "[а-яА-Яa-zA-Z]";

	// число
	final static String NUMBER_ST = "[0-9]+";

	// имеет № углов
	static String HAS_NVERTS = "";

	// несколько фигур
	static String MANYFIGURES = "";

	// фигура в фигуре
	static String FIGURE_IN_FIGURE = "";

	// предложение
	final static String PROPOSITION = WORD_CHAR+"[^.]*(\\.|$)";

	// любое упоминание геометрических примитивов
	static String ANY_FIGURE;

	// любое отношение между фигурами
	static String ANY_RELATION = "";

	// пред-свойство
	static String ANY_PREPROPERTY = "";
	static String ANY_PREPROPERTY2 = "";

	// пред-свойства и название фигуры
	static String PROPERTY_FIGURE;

	// выполняется при запуске приложения
	public static void main(String[] args) throws ClassNotFoundException
	{
		int error = 0;

		String jdbcLibraryName = "sqlite-jdbc-3.7.2.jar";
		String dbName = "onto.db";

		if (!(new File(jdbcLibraryName)).exists())
		{
			// отсутствует библиотека
			JOptionPane.showMessageDialog(null, "Отсутствует поставляемая вместе с приложением библиотека sqlite-jdbc-3.7.2.jar\nБиблиотеку можно скачать по следующему адресу https://bitbucket.org/xerial/sqlite-jdbc/downloads/sqlite-jdbc-3.7.2.jar", "Ошибка", JOptionPane.ERROR_MESSAGE);
			error = 3;
		}
		else // если библиотеки найдены
		{
			Log("Работаем с базой данных");

			// если файл БД существует
			if ((new File(dbName)).exists())
			{
				// пытаемся считать информацию из БД
				if (!readDataBase())
				{
					// не вышло
					error = 1;
					TextToGraphic.Log("Не удалось прочитать информацию из БД.\nВозможно она была повреждена.");
					JOptionPane.showMessageDialog(null, "Не удалось прочитать информацию из БД.\nВозможно она была повреждена.", "Ошибка", JOptionPane.ERROR_MESSAGE);
				}
			}
			else
			{
				TextToGraphic.Log("Не нашли файл базы данных. Попытка восстановить стандартную базу..");
				// пытаемся восстановить стандатрную базу
				if (createNewDataBase())
				{
					// если вышло создать, пробуем подключиться
					if (!readDataBase())
					{
						// восстановленная БД не соответствует необходимой структуре
						// пишем в консоль об ошибке
						TextToGraphic.Log("Не удалось воссоздать работоспособную БД. Приложение будет закрыто.");
						JOptionPane.showMessageDialog(null, "Не удалось воссоздать работоспособную БД. Приложение будет закрыто.", "Ошибка", JOptionPane.ERROR_MESSAGE);
						error = 2;
					}
					else
					{
						TextToGraphic.Log("Успешно!");
						// нам удалось восстановить БД и считать из неё информацию
						error = 0;
					}
				}
				else // если у нас не вышло создать новый файл БД
				{
					try
					{
						// проверка на возможность записи в каталог
						FileWriter fw = new FileWriter("testexamplefile.test");
						fw.write(" ");
						fw.flush();
						fw.close();
						File f = new File("testexamplefile.test");
						f.delete();

						TextToGraphic.Log("Не удалось восстановить БД. Удалите файл базы данных и перезапустите приложение.");
						JOptionPane.showMessageDialog(null, "Не удалось восстановить БД. Удалите файл базы данных и перезапустите приложение.", "Ошибка", JOptionPane.ERROR_MESSAGE);
						error = 1;
					}
					catch (IOException ex)
					{
						TextToGraphic.Log("Не удалось восстановить БД. Каталог защищён от записи.");
						JOptionPane.showMessageDialog(null, "Не удалось восстановить БД. Каталог защищён от записи.", "Ошибка", JOptionPane.ERROR_MESSAGE);
						error = 4;
					}
				}
			}
		}

		if (error == 0)
		{
			Log("Открытие окна");
			// создаём новое окно и указываем заголовок
			Window1 f = new Window1("Чертить");
			f.setSize(980, 450);
			f.setVisible(true);
			Log("Приложение готово к вводу");
		}
		else
		{
			// если приложение не удалось запустить
			System.exit(0);
		}
	}

	// создаём стандартные записи в БД
	public static boolean createNewDataBase() throws ClassNotFoundException
	{
		int error = 0;

		// подключаем драйвер для работы с SQLite
		Class.forName("org.sqlite.JDBC");

		Connection connection = null;
		try
		{
			// создаём подключение к БД
			connection = DriverManager.getConnection("jdbc:sqlite:onto.db");
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);

			// уничтожаем таблицу FIGURES, если такая есть
			statement.executeUpdate("drop table if exists FIGURES");
			// создаём таблицу
			statement.executeUpdate("create table FIGURES (ID_FIG integer, PARENT integer, NAME string, PADEGE_TYPE integer)");
			// добавляем записи
			statement.executeUpdate("insert into FIGURES values(0, -1, 'фигур', 1)");
			statement.executeUpdate("insert into FIGURES values(1, 0, 'овал', 0)");
			statement.executeUpdate("insert into FIGURES values(2, 1, 'эллипс', 0)");
			statement.executeUpdate("insert into FIGURES values(3, 2, 'круг', 0)");
			statement.executeUpdate("insert into FIGURES values(4, 0, 'полигон', 0)");
			statement.executeUpdate("insert into FIGURES values(5, 4, 'четырёхугольник', 0)");
			statement.executeUpdate("insert into FIGURES values(6, 5, 'прямоугольник', 0)");
			statement.executeUpdate("insert into FIGURES values(7, 6, 'квадрат', 0)");
			statement.executeUpdate("insert into FIGURES values(8, 4, 'треугольник', 0)");
			statement.executeUpdate("insert into FIGURES values(9, 4, 'многоугольник', 0)");
			// тест пользовательских свойств
			statement.executeUpdate("insert into FIGURES values(10, 4, 'звезд', 1)");

			// уничтожаем таблицу PADEGES, если такая есть
			statement.executeUpdate("drop table if exists PADEGES");
			// создаём таблицу
			statement.executeUpdate("create table PADEGES (PADEGE_TYPE integer, INAME string, RNAME string, DNAME string, VNAME string, TNAME string, PNAME string)");
			// добавляем записи
			statement.executeUpdate("insert into PADEGES values(0, '', 'а', 'у', '', 'ом', 'е')");
			statement.executeUpdate("insert into PADEGES values(1, 'а', 'ы', 'е', 'у', 'ой', 'е')");

			// уничтожаем таблицу PROPERTIES, если такая есть
			statement.executeUpdate("drop table if exists PROPERTIES");
			// создаём таблицу
			statement.executeUpdate("create table PROPERTIES (ID_PROP integer, ID_FIG integer, PROPERTY integer, VALUE integer)");
			// добавляем записи
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
			// тест пользовательских свойств
			statement.executeUpdate("insert into PROPERTIES values(11, 10, 20, 0)");

			// уничтожаем таблицу PROPNAMES, если такая есть
			statement.executeUpdate("drop table if exists PROPNAMES");
			// создаём таблицу
			statement.executeUpdate("create table PROPNAMES (PROPERTY integer, NAME string)");
			// добавляем записи
			statement.executeUpdate("insert into PROPNAMES values(0, 'маленький|маленькая|маленьком|маленькой|маленькую!нормальный|нормальная!большой|большая|большом|большой')");
			statement.executeUpdate("insert into PROPNAMES values(1, 'не симметричная|не симметричный!симметричная|симметрична|симметричной|симметричный|симметричную|симметричного')");
			statement.executeUpdate("insert into PROPNAMES values(2, 'разомкнута|разомкнутый|не замкнутая|не замкнута|не замкнутой!замкнутый|замкнут|замкнутая|замкнута|замкнутую')");
			statement.executeUpdate("insert into PROPNAMES values(3, 'имеет|имеющий|имеющая|имеющего|имеющую!угол|угла|углов|вершину|вершины|вершин')");
			statement.executeUpdate("insert into PROPNAMES values(4, 'имеющий как минимум|имеющая как минимум|имеющую как минимум')");
			statement.executeUpdate("insert into PROPNAMES values(5, 'имеющий максимум|имеющая максимум|имеющую максимум')");
			// тест пользовательских свойств
			statement.executeUpdate("insert into PROPNAMES values(20, 'звезданутый')");

			// уничтожаем таблицу RELATIONS, если такая есть
			statement.executeUpdate("drop table if exists RELATIONS");
			// создаём таблицу
			statement.executeUpdate("create table RELATIONS (RELATION integer, NAME string)");
			// добавляем записи
			statement.executeUpdate("insert into RELATIONS values(1, 'в |внутри ')");
			statement.executeUpdate("insert into RELATIONS values(2, 'пересекает ')");
			statement.executeUpdate("insert into RELATIONS values(3, 'сверху от |над ')");
			statement.executeUpdate("insert into RELATIONS values(4, 'снизу от |под ')");
			statement.executeUpdate("insert into RELATIONS values(5, 'слева от |слева ')");
			statement.executeUpdate("insert into RELATIONS values(6, 'справа от |справа ')");
		}
		catch(SQLException e)
		{
			error = 1;
			// не удалось записать информацию в БД
			//System.err.println(e.getMessage());
		}
		finally
		{
			try
			{
				if (connection != null)
					connection.close();
			}
			catch(SQLException e)
			{
				// если не удалось закрыть подключение к БД
				//System.err.println(e);
			}
			finally
			{
				if (error != 0)
					return false;
				else
					return true;
			}
		}
	}

	// считываем данные из БД
	public static boolean readDataBase() throws ClassNotFoundException
	{
		int error = 0;

		// подключаем драйвер для работы с SQLite
		Class.forName("org.sqlite.JDBC");

		Connection connection = null;
		try
		{
			// создаём подключение к БД
			connection = DriverManager.getConnection("jdbc:sqlite:onto.db");
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);

			// узнаём количество фигур в БД
			ResultSet rs = statement.executeQuery("select count(*) as COUNT from FIGURES");
			while(rs.next())
			{
				FCount = rs.getInt("COUNT");
			}

			// выделяем память под фигуры
			FNames = new String[FCount][8];
			ProtoFigures = new ProtoFigure[FCount];

			// забираем всю информацию о фигурах из таблицы FIGURES
			int id;
			String name;
			SOME_FIGURE = "";
			rs = statement.executeQuery("select FIG.ID_FIG, FIG.PARENT, FIG.NAME, PAD.INAME, PAD.RNAME, PAD.DNAME, PAD.VNAME, PAD.TNAME, PAD.PNAME from FIGURES as FIG, PADEGES as PAD where FIG.PADEGE_TYPE = PAD.PADEGE_TYPE");
			int count = 0;
			while(rs.next())
			{
				id = rs.getInt("ID_FIG");
				FNames[id][0] = rs.getString("NAME");
				FNames[id][1] = rs.getString("INAME");
				FNames[id][2] = rs.getString("RNAME");
				FNames[id][3] = rs.getString("DNAME");
				FNames[id][4] = rs.getString("VNAME");
				FNames[id][5] = rs.getString("TNAME");
				FNames[id][6] = rs.getString("PNAME");
				FNames[id][7] = FNames[id][1] + "|" + FNames[id][2] + "|" + FNames[id][3] + "|" + FNames[id][4] + "|" + FNames[id][5] + "|" + FNames[id][6];

				SOME_FIGURE += (id > 0 ? ")|((" : "(") + FNames[id][0] + ")(" + FNames[id][7] + ")";
				ProtoFigures[id] = new ProtoFigure(id, rs.getInt("PARENT"));

				count++;
			}

			// узнаём количество имён фигур
			rs = statement.executeQuery("select max(PROPERTY) as MAX from PROPNAMES");
			while(rs.next())
			{
				PCount = rs.getInt("MAX") + 1;
				if (PCount > Property.EMPTY_PROPERTY)
					PCount += Property.EMPTY_PROPERTY - 19;
			}

			// выделяем память
			PropNames = new String[PCount][3];

			// забираем все имена свойств фигур
			rs = statement.executeQuery("select * from PROPNAMES");
			String NextString;
			while(rs.next())
			{
				// достаём ID
				id = rs.getInt("PROPERTY");
				// нормализуем ID
				if (id > Property.EMPTY_PROPERTY)
					id += Property.EMPTY_PROPERTY - 19;

				NextString = rs.getString("NAME");
				int i = 0;
				while (hasStringLikeThis(NextString, "!") && i < 3)
				{
					PropNames[id][i] = getStartStringLikeThis(NextString, "!");
					if (i < 3 || i > 5)
						SOME_PREPROPERTY += (id > 0 || i > 0 ? "|" : "") + PropNames[id][i];
					NextString = getEndStringLikeThis(NextString, "!");
					i++;
				}
				PropNames[id][i] = NextString;
				if (i < 3 || i > 5)
					SOME_PREPROPERTY += (id > 0 || i > 0 ? "|" : "") + PropNames[id][i];
			}

			// забираем все свойства фигур
			rs = statement.executeQuery("select ID_FIG, PROPERTY, VALUE from PROPERTIES");
			int property;
			while(rs.next())
			{
				property = rs.getInt("PROPERTY");
				if (property > Property.EMPTY_PROPERTY)
					property += Property.EMPTY_PROPERTY - 19;
				ProtoFigures[rs.getInt("ID_FIG")].addProperty(new Property(property, rs.getInt("VALUE")));
			}

			// добавляем для каждой протофигуры свойства всех её родителей
			for (int i = 0; i < FCount; i++)
			{
				// если это не базовый класс
				if (ProtoFigures[i].parent() != -1)
				{	// добавляем свойства от прородителя
					ProtoFigures[i].addPropertiesFromAnotherPF(ProtoFigures[ProtoFigures[i].parent()]);
				}
			}

			// узнаём количество отношений в БД
			rs = statement.executeQuery("select count(*) as COUNT from RELATIONS");
			while(rs.next())
			{
				RCount = rs.getInt("COUNT");
			}

			// выделяем память под отношения
			RelNames = new String[RCount];

			// забираем всю информацию о отношениях между фигурами из таблицы RELATIONS
			rs = statement.executeQuery("select * from RELATIONS");
			while(rs.next())
			{
				id = rs.getInt("RELATION") - 1;
				RelNames[id] = rs.getString("NAME");
				SOME_RELATION += (id > 0 ? "|" : "") + RelNames[id];
			}

			// задаём регулярные выражения, зависящие от данных из БД
			SOME_FIGURE = "(("+SOME_FIGURE+"))";
			ANY_FIGURE = "(^|[\\s])+"+SOME_FIGURE+"([\\s]|\\.|,|$)";
			ANY_RELATION = "(^)((,|[\\s]|[^.])*[\\s])*("+SOME_RELATION+")[^.]*"+SOME_FIGURE+"([\\s]|\\.|,|$)";
			ANY_PREPROPERTY = "(^|[\\s])+("+SOME_PREPROPERTY+")([\\s]|\\.|,|$)+";
			ANY_PREPROPERTY2 = "(^|[\\s])*("+SOME_PREPROPERTY+")([\\s]|\\.|,|$)+";
			PROPERTY_FIGURE = "[\\s]*(("+SOME_PREPROPERTY+")[^.]*)+[\\s]+"+SOME_FIGURE+"([\\s]|\\.|,|$)";
			HAS_NVERTS = "(^)(,|[\\s])*("+PropNames[3][0]+")[\\s]+[0-9]+[\\s]+("+PropNames[3][1]+")([\\s]|\\.|,|$)";
			MANYFIGURES = "(^)(,|[\\s])*[0-9]+(("+SOME_PREPROPERTY+")[^.]*)*("+SOME_FIGURE+")([\\s]|\\.|,|$)";
		}
		catch(SQLException e)
		{
			error = 1;
			// не удалось считать информацию из БД
			//System.err.println(e.getMessage());
		}
		finally
		{
			try
			{
				if (connection != null)
					connection.close();
			}
			catch(SQLException e)
			{
				// если не удалось закрыть подключение к БД
				//System.err.println(e);
			}
			finally
			{
				if (error != 0)
					return false;
				else
					return true;
			}
		}
	}

	// вывод лога с временным штампом в консоль
	public static void Log(String text)
	{
		System.out.println(new java.text.SimpleDateFormat("HH:mm:ss,S").format(java.util.Calendar.getInstance().getTime())+" Log: "+text);
	}

	// анализировать текст
	public static String AnalyseText(String st, TextArea ta, FiguresMass fr)
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

				// оставшаяся строка
				String Next_String = Propn;
				// обрабатываемая строка
				String This_String;
				// чистое название фигуры
				String FigureName;
				// чистое название свойства
				String PropertyName;
				// вспомогательная строка
				String nextCroppedString;
				// первая ли это фигура в коллекции
				boolean isFirstFigure = true;

				// для каждой фигуры
				for (int j = 0; j < getCountOfStringsLikeThis(Propn, ANY_FIGURE); j++)
				{
					This_String = getStartIncStringLikeThis(Next_String, ANY_FIGURE);
					Next_String = getEndStringLikeThis(Next_String, ANY_FIGURE);

					// создаём фигуру и выводим её название
					FigureName = getStringLikeThis(getStringLikeThis(Propn, ANY_FIGURE, j), SOME_FIGURE);
					Figure thisFigure = new Figure(ProtoFigures[getFigureID(FigureName)]);
					ta.append("Обнаружена фигура: "+FigureName+"\n");

					// если содержатся пред-свойства
					if (hasStringLikeThis(This_String, PROPERTY_FIGURE))
					{
						// находим все пред-свойства
						String PropertiesString = "Свойства фигуры \""+FigureName+"\": ";
						for (int k = 0; k < getCountOfStringsLikeThis(This_String, ANY_PREPROPERTY2); k++)
						{
							// находим имя, очищенное от символов
							PropertyName = getStringLikeThis(getStringLikeThis(This_String, ANY_PREPROPERTY2, k), SOME_PREPROPERTY);
							// если это нужное свойство
							Property newProperty = getPropertyByName(PropertyName);
							if ((newProperty.type() < 3 || newProperty.type() > 5) && newProperty.type() != Property.EMPTY_PROPERTY)
							{
								// добавляем фигуре свойство
								thisFigure.addProperty(newProperty);
								PropertiesString += PropertyName + " ";
							}
						}
						ta.append(PropertiesString+"\n");
					}

					// отрезаем от следующей строки кусок до следующей фигуры
					nextCroppedString = getStartIncStringLikeThis(Next_String, ANY_FIGURE);
					// если содержится информация о количестве вершин
					if (hasStringLikeThis(nextCroppedString, HAS_NVERTS))
					{
						int numvert = Integer.parseInt(getStringLikeThis(getStringLikeThis(Next_String, HAS_NVERTS), NUMBER_ST));
						thisFigure.addProperty(new Property(3, numvert));
						ta.append("Фигура \""+FigureName+"\" имеет "+numvert+" вершин.\n");

						// урезаем строку, чтобы не обработать свойство второй раз
						Next_String = getEndStringLikeThis(Next_String, HAS_NVERTS);
					}

					// определяем класс нашей фигуры
					if (thisFigure.refreshClass())
					{	// если фигура изменила класс, то сообщаем об этом
						ta.append("Под описание подходит: "+FNames[thisFigure.fclass()][0]+FNames[thisFigure.fclass()][1]+"\n");
					}

					// смотрим, нет ли упоминания о количестве фигур
					if (hasStringLikeThis(This_String, MANYFIGURES))
					{
						String FigureName2 = getStringLikeThis(getStringLikeThis(This_String, MANYFIGURES), SOME_FIGURE);
						if (FigureName.equals(FigureName2))
						{
							ta.append("Оле\n");
							int fCount = Integer.parseInt(getStringLikeThis(getStringLikeThis(This_String, MANYFIGURES), NUMBER_ST));
							for (int k = 0; k < fCount - 1; k++)
							{
								fr.insertFigure(thisFigure);
							}
						}
					}

					// если это вторая фигура в коллекции
					if (!isFirstFigure)
					{
						// добавляем информацию о фигуре в последнюю обработанную коллекцию
						fr.getFigure(fr.count() - 1).addSecondFigure(thisFigure);
						isFirstFigure = true;
					}
					else if (hasStringLikeThis(nextCroppedString, ANY_RELATION))
					{	// если содержится информация об отношении этой фигуры с другой фигурой

						// вносим первую фигуру в коллекцию
						fr.insertFigure(thisFigure);
						// получаем тип отношения
						int relation = getRelationByName(getStringLikeThis(getStringLikeThis(Next_String, ANY_RELATION), SOME_RELATION));
						// устонавливаем тип отношения
						fr.getFigure(fr.count() - 1).setType(relation);

						// урезаем строку, чтобы не обработать свойство второй раз
						Next_String = getEndStringLikeThis(Next_String, SOME_RELATION);

						isFirstFigure = false;
					}
					else // если это простая одиночная фигура
					{
						// добавляем фигуру в список на отрисовку
						fr.insertFigure(thisFigure);
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
		int count = 0;
		Pattern p = Pattern.compile(mask, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(st.toLowerCase());
		while (m.find())
		{
			count++;
		}
		return count;
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

	// узнать индекс символа начала вхождения n-ного выражения, соответствующего шаблону
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

	// узнать индекс символа конца вхождения n-ного выражения, соответствующего шаблону
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

	// вернуть строку, которая предшествует первому выражению, соответствующему шаблону
	private static String getStartStringLikeThis(String st, String mask)
	{
		int start = getStartPosStringLikeThis(st, mask, 0);
		if (start != -1)
			return st.substring(0, start);
		else
			return st;
	}

	// (синоним) вернуть строку, которая предшествует n-ному выражению, соответствующему шаблону
	private static String getStartStringLikeThis(String st, String mask, int ordNumber)
	{
		int start = getStartPosStringLikeThis(st, mask, ordNumber);
		if (start != -1)
			return st.substring(0, start);
		else
			return st;
	}

	// вернуть строку, которая предшествует (и содержит) первому выражению, соответствующему шаблону
	private static String getStartIncStringLikeThis(String st, String mask)
	{
		int end = getEndPosStringLikeThis(st, mask, 0);
		if (end != -1)
			return st.substring(0, end);
		else
			return st;
	}

	// (синоним) вернуть строку, которая предшествует (и содержит) n-ному выражению, соответствующему шаблону
	private static String getStartIncStringLikeThis(String st, String mask, int ordNumber)
	{
		int end = getEndPosStringLikeThis(st, mask, ordNumber);
		if (end != -1)
			return st.substring(0, end);
		else
			return st;
	}

	// вернуть строку, которая находится после первого выражения, соответствующему шаблону
	private static String getEndStringLikeThis(String st, String mask)
	{
		int end = getEndPosStringLikeThis(st, mask, 0);
		if (end != -1)
			return st.substring(end, st.length());
		else
			return st;
	}

	// (синоним) вернуть строку, которая находится после n-ного выражения, соответствующего шаблону
	private static String getEndStringLikeThis(String st, String mask, int ordNumber)
	{
		int end = getEndPosStringLikeThis(st, mask, ordNumber);
		if (end != -1)
			return st.substring(end, st.length());
		else
			return st;
	}

	// определить ID фигуры
	private static int getFigureID(String FigureName)
	{
		for (int i  = 0; i < FCount; i++)
		{
			// ищем название фигуры в любом из падежей
			if (hasStringLikeThis(FigureName, "("+FNames[i][0]+")("+FNames[i][7]+")"))
				return i;
		}
		return -1;
	}

	// определить свойство по названию
	private static Property getPropertyByName(String PropertyName)
	{
		for (int i = 0; i < PCount; i++)
		{
			if (i < 3 || i > 5)
			{
				for (int j = 0; j < 3; j++)
				{
					if (PropNames[i][j] != null && hasStringLikeThis(PropertyName, PropNames[i][j]))
					{
						return new Property(i, j);
					}
				}
			}
		}

		return new Property();
	}

	// определить отношение по названию
	private static int getRelationByName(String RelName)
	{
		for (int i = 0; i < RCount; i++)
		{
			// ищем название отношения в любом из падежей
			if (hasStringLikeThis(RelName, RelNames[i]))
				return i + 1;
		}
		return -1;
	}
}

// обработчик событий
class ActLis implements ActionListener, TextListener
{
	private TextArea tf;
	private Label lb;
	private TextArea ta;
	private FiguresMass fr;
	private Window1 window;

	// при создании обработчика
	ActLis(Window1 window)
	{
		this.window = window;
		this.tf = window.Field;
		this.lb = window.Label1;
		this.ta = window.Area;
		this.fr = window.Figures;
	}

	// при нажатии кнопки "Распознать"
	public void actionPerformed(ActionEvent ae)
	{
		ta.replaceRange("", 0, 10000);
		fr.reset();
		lb.setText(TextToGraphic.AnalyseText(tf.getText(), ta, fr));
		window.update(window.getGraphics());
		window.paint(window.getGraphics());
	}

	// при изменении текста
	public void textValueChanged(TextEvent e)
	{
		if (window.CB.getState())
		{
			ta.replaceRange("", 0, 10000);
			fr.reset();
			lb.setText(TextToGraphic.AnalyseText(tf.getText(), ta, fr));
			window.update(window.getGraphics());
			window.paint(window.getGraphics());
		}
	}
}