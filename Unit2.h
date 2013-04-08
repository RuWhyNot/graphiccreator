//---------------------------------------------------------------------------

#ifndef Unit2H
#define Unit2H
//---------------------------------------------------------------------------
#include <Classes.hpp>
#include <Controls.hpp>
#include <StdCtrls.hpp>
#include <Forms.hpp>
#include <Menus.hpp>
//---------------------------------------------------------------------------
class TDM : public TDataModule
{
__published:	// IDE-managed Components
  TMainMenu *MainMenu1;
  TMenuItem *N1;
  TMenuItem *N2;
  void __fastcall N2Click(TObject *Sender);
private:	// User declarations
public:		// User declarations
  __fastcall TDM(TComponent* Owner);
};
//---------------------------------------------------------------------------
extern PACKAGE TDM *DM;
//---------------------------------------------------------------------------
#endif
