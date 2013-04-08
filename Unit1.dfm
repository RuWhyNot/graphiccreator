object Form1: TForm1
  Left = 282
  Top = 277
  Width = 825
  Height = 536
  Caption = 'Form1'
  Color = clBtnFace
  Font.Charset = DEFAULT_CHARSET
  Font.Color = clWindowText
  Font.Height = -11
  Font.Name = 'MS Sans Serif'
  Font.Style = []
  Menu = DM.MainMenu1
  OldCreateOrder = False
  OnActivate = FormActivate
  OnCreate = FormCreate
  PixelsPerInch = 96
  TextHeight = 13
  object PageControl1: TPageControl
    Left = 0
    Top = 0
    Width = 817
    Height = 490
    ActivePage = TabSheet1
    Align = alClient
    TabIndex = 0
    TabOrder = 0
    object TabSheet1: TTabSheet
      Caption = #1054#1085#1090#1086#1083#1086#1075#1080#1080
      object Label1: TLabel
        Left = 72
        Top = 88
        Width = 39
        Height = 13
        Caption = #1060#1080#1075#1091#1088#1072
      end
      object Label2: TLabel
        Left = 240
        Top = 88
        Width = 90
        Height = 13
        Caption = #1050#1086#1083#1080#1095#1077#1089#1090#1074#1086' '#1091#1075#1083#1086#1074
      end
      object Label3: TLabel
        Left = 360
        Top = 88
        Width = 39
        Height = 13
        Caption = #1056#1072#1079#1084#1077#1088
      end
      object CB_Figures: TComboBox
        Left = 72
        Top = 104
        Width = 145
        Height = 21
        ItemHeight = 13
        TabOrder = 0
        OnChange = CB_FiguresChange
      end
      object Ed_VertCount: TEdit
        Left = 240
        Top = 104
        Width = 97
        Height = 21
        TabOrder = 1
      end
      object ChB_Valid: TCheckBox
        Left = 72
        Top = 152
        Width = 97
        Height = 17
        Caption = #1055#1088#1072#1074#1080#1083#1100#1085#1099#1081
        TabOrder = 2
      end
      object CB_Size: TComboBox
        Left = 360
        Top = 104
        Width = 145
        Height = 21
        ItemHeight = 13
        TabOrder = 3
      end
    end
  end
end
