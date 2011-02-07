/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2005  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
 *
 *See COPYING for Details
 *
 *This program is free software; you can redistribute it and/or
 *modify it under the terms of the GNU General Public License
 *as published by the Free Software Foundation; either version 2
 *of the License, or (at your option) any later version.
 *
 *This program is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with this program; if not, write to the Free Software
 *Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * Created on 25.04.2005
 */
/**
*	All the icons used in the browser are defined here  straight coded with actionscript sentences
*/
class visorFreeMind.Icons {

	// draw ovals/circles
	static function circle(x,y,width,height,lineWidth,borderColor,color,ref_mc){
		var a=width;
		var b=height;
		var j=a*0.70711;
		var n=b*0.70711;
		var i=j-(b-n)*a/b;
		var m=n-(a-j)*b/a;
		ref_mc.lineStyle(lineWidth,borderColor,100);
		ref_mc.beginFill(color,100);
		ref_mc.moveTo(x+a,y);
		ref_mc.curveTo(x+a,y-m,x+j,y-n);
		ref_mc.curveTo(x+i,y-b,x,y-b);
		ref_mc.curveTo(x-i,y-b,x-j,y-n);
		ref_mc.curveTo(x-a,y-m,x-a,y);
		ref_mc.curveTo(x-a,y+m,x-j,y+n);
		ref_mc.curveTo(x-i,y+b,x,y+b);
		ref_mc.curveTo(x+i,y+b,x+j,y+n);
		ref_mc.curveTo(x+a,y+m,x+a,y);
		ref_mc.endFill();
	}

	static public  function get_ksmiletris(mc_padre,depth){ //I am happy
		var smile=mc_padre.createEmptyMovieClip("happy",6+depth);
		var color=0xFEF232;
		var colorBorde=0xFDA600;
		var borderColor=0x777777;
		circle(7,7,7,7,1,borderColor,color,smile);
		smile.lineStyle(2,0x555555,100);
		smile.moveTo(5,4);
		smile.lineTo(5,6);
		smile.moveTo(9,4);
		smile.lineTo(9,6);
		smile.lineStyle(1,0x555555,100);
		smile.moveTo(3,8);
		smile.curveTo(4,10,6,11);
		smile.curveTo(10,11,11,8);
		return smile;
	}
	
	static public  function get_smily_bad(mc_padre,depth){ //I am happy
		var smile=mc_padre.createEmptyMovieClip("sad",6+depth);
		var color=0xFEF232;
		var colorBorde=0xFDA600;
		var borderColor=0x777777;
		circle(7,7,7,7,1,borderColor,color,smile);
		smile.lineStyle(2,0x555555,100);
		smile.moveTo(5,4);
		smile.lineTo(5,6);
		smile.moveTo(9,4);
		smile.lineTo(9,6);
		smile.lineStyle(1,0x555555,100);
		smile.moveTo(3,11);
		smile.curveTo(4,10,6,9);
		smile.curveTo(10,9,11,11);
		return smile;
	}

	static public  function get_gohome(mc_padre,depth){ //Home
		var home=mc_padre.createEmptyMovieClip("home",6+depth);

		var color=0xFFFFFF;
		var colorBorde=0x3B5668;
		var door=0xFFA858;
		var tejado=0xFF0000;

		home.lineStyle(1,colorBorde,100);
		home.beginFill(color,100);
		home.moveTo(2,14);
		home.lineTo(2,6);
		home.lineTo(7,3);
		home.lineTo(12,6);
		home.lineTo(12,14);
		home.lineTo(2,14);
		home.endFill();

		home.lineStyle(3,0x000000,100);
		home.moveTo(1,6);
		home.lineTo(7,2);
		home.lineTo(13,6);
		home.lineStyle(2,tejado,100);
		home.moveTo(1,6);
		home.lineTo(7,2);
		home.lineTo(13,6);

		home.lineStyle(4,0x000000,100);
		home.moveTo(7,12);
		home.lineTo(7,9);
		home.lineStyle(3,door,100);
		home.moveTo(7,11);
		home.lineTo(7,9);
		return home;
	}

	static public  function get_help(mc_padre,depth){ //Question
		var question=mc_padre.createEmptyMovieClip("question",6+depth);

		var color=0x0000FF;

		question.lineStyle(3,0x000000,100);
		question.moveTo(3,6);
		question.curveTo(1,3,3,1);
		question.curveTo(12,0,12,3);
		question.curveTo(12,5,8,8);
		question.curveTo(6,8,7,10);
		question.lineStyle(3,0x000000,100);
		question.moveTo(7,13);
		question.lineTo(7,12.8);

		question.lineStyle(2,color,100);
		question.moveTo(3,6);
		question.curveTo(1,3,3,1);
		question.curveTo(12,0,12,3);
		question.curveTo(12,5,8,8);
		question.curveTo(6,8,7,10);
		question.lineStyle(2,color,100);
		question.moveTo(7,13);
		question.lineTo(7,12.8);

		return question;
	}

	static public  function get_pencil(mc_padre,depth){ //To be refined
		var pencil=mc_padre.createEmptyMovieClip("pencil",6+depth);

		var color=0xC00000;

		pencil.lineStyle(1,0xDADADA,100);
		pencil.beginFill(0xFAFAFA,100);
		pencil.moveTo(0,14);
		pencil.lineTo(6,11);
		pencil.lineTo(3,7);
		pencil.lineTo(0,14);
		pencil.endFill();
		pencil.lineStyle(2,color,100);
		pencil.moveTo(11,0);
		pencil.lineTo(3,8);
		pencil.moveTo(12,1);
		pencil.lineTo(4,9);
		pencil.moveTo(13,2);
		pencil.lineTo(5,10);
		pencil.lineStyle(0,0xEEEEEE,100);
		pencil.moveTo(11.5,0.5);
		pencil.lineTo(3.5,8.5);
		pencil.lineStyle(1,0x888888,100);
		pencil.moveTo(10,0);
		pencil.lineTo(2,8);
		pencil.lineStyle(1,0x000000,100);
		pencil.moveTo(0.6,14);
		pencil.lineTo(6,11);
		pencil.lineTo(14,3);
		pencil.lineStyle(2,color,100);
		pencil.moveTo(0,14);
		pencil.lineTo(1,13);

		return pencil;
	}

	static public  function get_bell(mc_padre,depth){ //Remember
		var remember=mc_padre.createEmptyMovieClip("remember",6+depth);

		var bell=0xADAC83;
		var wood=0xAE7A46;

		remember.lineStyle(4,bell,100);
		remember.moveTo(5,12);
		remember.lineTo(9,12);
		remember.moveTo(6,9);
		remember.lineTo(8,9);
		remember.lineStyle(3,0xEDECCC,100);
		remember.moveTo(6,12);
		remember.lineTo(6,9);

		remember.lineStyle(1,0x555555,100);
		remember.moveTo(6,6);
		remember.lineTo(4,8);
		remember.lineTo(3,13);
		remember.lineTo(2,13);
		remember.lineTo(4,14);
		remember.lineTo(10,14);
		remember.lineTo(12,13);
		remember.lineTo(11,13);
		remember.lineTo(10,8);
		remember.lineTo(8,6);
		remember.lineStyle(3,0x555555,100);
		remember.moveTo(7,6);
		remember.lineTo(7,2);
		remember.lineStyle(2,wood,100);
		remember.moveTo(7,8);
		remember.lineTo(7,2);

		return remember;
	}

	static public  function get_penguin(mc_padre,depth){ //Linux
		var linux=mc_padre.createEmptyMovieClip("linux",6+depth);
		var leg=0xEAEA05;
		circle(7,8,4,4,1,0x222222,0x222222,linux);
		linux.lineStyle(4,0x222222,100);
		linux.moveTo(7,7);
		linux.lineTo(7,2);
/*		linux.lineStyle(8,0x222222,100);
		linux.moveTo(6.5,7);
		linux.lineTo(7.5,7);*/
		linux.lineStyle(7,0xEEEEEE,100);
		linux.moveTo(6.5,8.3);
		linux.lineTo(6.5,8);
		linux.lineStyle(4,0x222222,100);
		linux.moveTo(3,12);
		linux.lineTo(2,11);
		linux.moveTo(11,12);
		linux.lineTo(12,11);
		linux.lineStyle(3,leg,100);
		linux.moveTo(3,12);
		linux.lineTo(2,11);
		linux.moveTo(11,12);
		linux.lineTo(12,11);
		linux.moveTo(6.7,4);
		linux.lineTo(6.5,4.5);

		return linux;
	}

	static public  function get_idea(mc_padre,depth){ //Idea
		var idea=mc_padre.createEmptyMovieClip("idea",6+depth);
		var leg=0xEAEA05;
		circle(7,4,4,4,1,0x222222,0xFFFF00,idea);
		//idea.lineStyle(9,0x222222,100);
		//idea.moveTo(7,4);
		//idea.lineTo(7,4.4);
		idea.lineStyle(3,0x222222,100);
		idea.moveTo(6,7);
		idea.lineTo(6,12);
		idea.moveTo(8,7);
		idea.lineTo(8,12);
		idea.lineStyle(2,0x222222,0);
		idea.moveTo(2,7);
		idea.lineTo(12,13);
		idea.lineStyle(2,0x222222,100);
		idea.moveTo(6.6,7);
		idea.lineTo(6.6,13);
		idea.lineStyle(2,0x222222,100);
		idea.moveTo(7.3,7);
		idea.lineTo(7.3,13);
		//idea.lineStyle(7,0xFFFF00,100);
		//idea.moveTo(7,4);
		//idea.lineTo(7,4.4);
		idea.lineStyle(4,0xFFFF00,100);
		idea.moveTo(7,6);
		idea.lineTo(7,7);
		idea.lineStyle(0,0x555555,100);
		idea.moveTo(7,10);
		idea.lineTo(7,5);
		idea.lineTo(6,4);
		idea.moveTo(7,5);
		idea.lineTo(8,4);
		return idea;
	}

	static public  function get_licq(mc_padre,depth){ //Nice
		var nice=mc_padre.createEmptyMovieClip("nice",6+depth);
		var leaf=0x4DFE4D;
		nice.lineStyle(3.7,0x222222,80);
		nice.moveTo(7,7);
		nice.lineTo(3,7);
		nice.moveTo(7,7);
		nice.lineTo(11.5,7.5);
		nice.moveTo(7,7);
		nice.lineTo(7,2);
		nice.moveTo(7,7);
		nice.lineTo(7.5,12);
		nice.moveTo(7,7);
		nice.lineTo(3.5,3.5);
		nice.moveTo(7,7);
		nice.lineTo(3.5,11);
		nice.moveTo(7,7);
		nice.lineTo(11,11);
		nice.moveTo(7,7);
		nice.lineTo(11,3.5);

		nice.lineStyle(2.3,leaf,100);
		nice.moveTo(7,7);
		nice.lineTo(3,7);
		nice.moveTo(7,7);
		nice.lineTo(10.5,7);
		nice.moveTo(7,7);
		nice.lineTo(7,2);
		nice.moveTo(7,7);
		nice.lineTo(7,11);
		nice.moveTo(7,7);
		nice.lineTo(3.5,3.5);
		nice.moveTo(7,7);
		nice.lineTo(3.5,11);
		nice.moveTo(7,7);
		nice.lineTo(10,10.5);
		nice.moveTo(7,7);
		nice.lineTo(10,3.5);

		nice.lineStyle(4,0x888888,100);
		nice.moveTo(7,7);
		nice.lineTo(7,7.4);
		nice.lineStyle(3.5,0xFEE626,100);
		nice.moveTo(6.6,7);
		nice.lineTo(6.6,7.4);

return nice;
	}

	static public  function get_password(mc_padre,depth){ //Key
		var key=mc_padre.createEmptyMovieClip("key",6+depth);
		var leaf=0xFEE626;
		var oscuro=0xBE7208;
		key.lineStyle(5,0x333333,80);
		key.moveTo(11.8,9.5);
		key.lineTo(11.8,12.5);
		key.lineStyle(2,oscuro,100);
		key.moveTo(1,2);
		key.lineTo(8,10);
		key.lineStyle(5,oscuro,100);
		key.moveTo(11,9);
		key.lineTo(11,12);
		key.lineStyle(2,leaf,100);
		key.moveTo(2,1.5);
		key.lineTo(9,9.5);
		key.moveTo(2,2);
		key.lineTo(2,7);
		key.moveTo(4,4);
		key.lineTo(4,9);
		key.lineStyle(3,leaf,100);
		key.moveTo(11,7);
		key.lineTo(11,12);

		key.lineStyle(1,oscuro,100);
		key.moveTo(2,4);
		key.lineTo(2,8);
		key.moveTo(4,6);
		key.lineTo(4,10);
		key.lineStyle(1,4,oscuro,100);
		key.moveTo(11,8);
		key.lineTo(11,12);

		return key;
	}

	static public  function get_korn(mc_padre,depth){ //Mailbox
		var mailbox=mc_padre.createEmptyMovieClip("mailbox",6+depth);
		mailbox.lineStyle(0,0x666666,100);
		mailbox.beginFill(0xBBBBBB,100);
		mailbox.moveTo(8,13);
		mailbox.lineTo(0,9);
		mailbox.lineTo(0,5);
		mailbox.curveTo(0,0,5,0);
		mailbox.lineTo(12,4);
		mailbox.lineTo(12,10);
		mailbox.lineTo(8,13);
		mailbox.endFill();

		mailbox.lineStyle(3,0xEEEEEE,100);
		mailbox.moveTo(2,3);
		mailbox.lineTo(9,7);
		mailbox.lineStyle(5,0x888888,100);
		mailbox.moveTo(10,6);
		mailbox.lineTo(10,8);
		mailbox.lineStyle(2,0xffffff,100);
		mailbox.moveTo(9.5,11);
		mailbox.lineTo(12,7);
		mailbox.lineStyle(3,0x777777,100);
		mailbox.moveTo(10,12);
		mailbox.lineTo(13,8);
		mailbox.lineStyle(1,0xEE0000,100);
		mailbox.moveTo(6,10);
		mailbox.lineTo(6,2);
		mailbox.moveTo(5,2);
		mailbox.lineTo(5,4);
		return mailbox;
	}

	static public  function get_desktop_new(mc_padre,depth){ //Do not forget
		var notForget=mc_padre.createEmptyMovieClip("notForget",6+depth);

		var color=0x0B5CC3;
		var colorBorde=0x3B5668;
		var yellow=0xFEF492;
		var azulClaro=0x6683B2;

		notForget.lineStyle(0,colorBorde,100);
		notForget.beginFill(color,100);
		notForget.moveTo(0,0);
		notForget.lineTo(13,0);
		notForget.lineTo(13,8);
		notForget.lineTo(2,13);
		notForget.lineTo(0,11);
		notForget.lineTo(0,0);
		notForget.endFill();
		notForget.lineStyle(1,azulClaro,100);
		notForget.moveTo(2,1);
		notForget.lineTo(2,12);
		notForget.lineStyle(1,yellow,100);
		notForget.moveTo(12,1);
		notForget.lineTo(12.5,0.5);
		notForget.moveTo(12,7);
		notForget.lineTo(12.5,7.5);
		notForget.lineStyle(2,yellow,100);
		notForget.moveTo(2,1);
		notForget.lineTo(1.4,1.2);
		notForget.moveTo(2.2,12);
		notForget.lineTo(1.5,11.7);

		notForget.lineStyle(1,yellow,0);
		notForget.moveTo(14,14);
		notForget.lineTo(13,13);
		return notForget;
	}

	static public  function get_xmag(mc_padre,depth){ // To be discussed
		var xmag=mc_padre.createEmptyMovieClip("xmag",6+depth);
		var color=0xEEEEEE;
		var colorBorde=0xFDA600;
		var borderColor=0x444444;
		circle(5,5,5,5,1,borderColor,color,xmag);
		xmag.lineStyle(1,borderColor,100);
		xmag.beginFill(0xAA7722,100);
		xmag.moveTo(12,14);
		xmag.lineTo(14,12);
		xmag.lineTo(10,7);
		xmag.lineTo(7,10);
		xmag.lineTo(12,14);
		xmag.endFill();
		return xmag;
	}

	static public  function get_clanbomber(mc_padre,depth){ //Dangerous
		var clanbomber=mc_padre.createEmptyMovieClip("clanbomber",6+depth);
		var color=0x111111;
		var colorBorde=0xFDA600;
		var borderColor=0x444444;
		circle(6,8,6,6,1,borderColor,color,clanbomber);
		clanbomber.lineStyle(2,0x4883EF,80);
		clanbomber.moveTo(3,5);
		clanbomber.lineTo(4,4)
		clanbomber.lineStyle(2,0xEC843B,100);
		clanbomber.moveTo(11,4);
		clanbomber.lineTo(13,0);
		clanbomber.lineStyle(2,0x444444,100);
		clanbomber.moveTo(11,4);
		clanbomber.lineTo(10,3);

		return clanbomber;
	}

	static public  function get_flag(mc_padre,depth){ //Flag
		var flag=mc_padre.createEmptyMovieClip("flag",6+depth);
		flag.lineStyle(4,0xDD3333,100);
		flag.moveTo(8,5);
		flag.lineTo(3,7);
		flag.lineStyle(3,0xFFFFFF,100);
		flag.moveTo(7,3);
		flag.lineTo(2,5);
		flag.lineStyle(2,0x333333,100);
		flag.moveTo(7,0);
		flag.lineTo(14,13);
		flag.lineStyle(1,0x333333,100);
		flag.moveTo(7,2);
		flag.lineTo(0,4);
		flag.lineTo(2,10);
		flag.lineTo(9,7);

		return flag;
	}

	static public  function get_knotify(mc_padre,depth){ //Music
		var knotify=mc_padre.createEmptyMovieClip("knotify",6+depth);
		knotify.lineStyle(4,0x333333,100);
		knotify.moveTo(2,6);
		knotify.lineTo(3,6);
		knotify.moveTo(7,5);
		knotify.lineTo(8,5);
		knotify.moveTo(6,13);
		knotify.lineTo(7,13);
		knotify.lineStyle(2,0xFFAA0A,100);
		knotify.moveTo(2,6);
		knotify.lineTo(3,6);
		knotify.moveTo(7,5);
		knotify.lineTo(8,5);
		knotify.moveTo(6,13);
		knotify.lineTo(7,13);

		knotify.lineStyle(1,0x333333,100);
		knotify.moveTo(3,6);
		knotify.lineTo(3,1);
		knotify.moveTo(8,5);
		knotify.lineTo(8,0);
		knotify.moveTo(7,13);
		knotify.lineTo(7,8);

		knotify.lineStyle(2,0x666666,100);
		knotify.moveTo(3,1);
		knotify.lineTo(8,0);
		knotify.moveTo(7,9);
		knotify.lineTo(11,8);

		return knotify;
	}

    static public function get_inner_link(mc_padre,depth){
    	return genLink(mc_padre,depth,0x00AA00);
    }

	static public  function get_link(mc_padre,depth){
    	return genLink(mc_padre,depth,0xEE0000);
	}
	static public  function genLink(mc_padre,depth,color){
		var link=mc_padre.createEmptyMovieClip("link",6+depth);
		link.lineStyle(1,color,100);
		link.beginFill(color,100);
		link.moveTo(2,6);
		link.lineTo(6,6);
		link.lineTo(6,4);
		link.lineTo(9,7.5);
		link.lineTo(6,11);
		link.lineTo(6,9);
		link.lineTo(2,9);
		link.lineTo(2,7);
		link.endFill();
		link.lineStyle(1,color,0);
		link.moveTo(0,0);
		link.lineTo(10,13);

		return link;
	}

	static public  function get_mm_link2(mc_padre,depth){ //butterfly
		var butterfly=mc_padre.createEmptyMovieClip("butterfly",6+depth);
		var color=0xEE0000;

		butterfly.lineStyle(0,0x00EE00,100);
		butterfly.moveTo(5,9);
		butterfly.curveTo(0,4,2,2)
		butterfly.curveTo(5,0,7,4)
		butterfly.moveTo(5,9);
		butterfly.curveTo(1,12,4,13)
		butterfly.curveTo(4,14,7,12)
		butterfly.lineStyle(0,0x0000EE,100);
		butterfly.moveTo(9,9);
		butterfly.curveTo(14,4,12,2)
		butterfly.curveTo(9,0,7,4)
		butterfly.moveTo(9,9);
		butterfly.curveTo(13,12,10,13)
		butterfly.curveTo(9,14,7,12)

		return butterfly;
	}

	static public  function get_back(mc_padre,depth){ //Back
		var back=mc_padre.createEmptyMovieClip("back",6+depth);
		var color=0x00CECE;
		back.lineStyle(1,0x555555,100);
		back.beginFill(color,100);
		back.moveTo(0,7);
		back.lineTo(7,0);
		back.lineTo(7,4);
		back.lineTo(13,4);
		back.lineTo(13,10);
		back.lineTo(7,10);
		back.lineTo(7,14);
		back.lineTo(0,7);
		back.endFill();
		back.lineStyle(0,0x555555,1);
		back.moveTo(14,14);
		back.lineTo(14,13);

		return back;
	}

	static public  function get_Mail(mc_padre,depth){ //Mail
		var Mail=mc_padre.createEmptyMovieClip("Mail",6+depth);
		var color=0xDDDBB9;
		Mail.lineStyle(1,0x555555,100);
		Mail.beginFill(color,100);
		Mail.moveTo(0,2);
		Mail.lineTo(14,2);
		Mail.lineTo(14,12);
		Mail.lineTo(0,12);
		Mail.lineTo(0,2);
		Mail.endFill();
		Mail.lineStyle(1,0x555555,100);
		Mail.moveTo(0,3);
		Mail.lineTo(7,9);
		Mail.lineTo(14,3);
		Mail.lineStyle(1,0x777777,100);
		Mail.moveTo(0,11);
		Mail.lineTo(5,7);
		Mail.moveTo(14,11);
		Mail.lineTo(9,7);

		return Mail;
	}

	static public  function get_Note(mc_padre,depth){ //Mail
		var Note=mc_padre.createEmptyMovieClip("Note",6+depth);
		var color=0xFFFF55;
		Note.lineStyle(1,0x666666,100);
		Note.beginFill(color,100);
		Note.moveTo(0,1);
		Note.lineTo(10,1);
		Note.lineTo(10,13);
		Note.lineTo(0,13);
		Note.lineTo(0,1);
		Note.endFill();
		Note.moveTo(2,4);
		Note.lineTo(9,4);
		Note.moveTo(2,6);
		Note.lineTo(9,6);
		Note.moveTo(2,8);
		Note.lineTo(9,8);
		Note.moveTo(2,10);
		Note.lineTo(7,10);

		return Note;
	}

	static public  function drawFlashDashLine(mc,s_x,s_y,dist,dl,sl){
		var x=s_x;
		var max_x=x+dist;
		while(max_x>(x+dl)){
			mc.moveTo(x,s_y);
			mc.lineTo(x+dl,s_y);
			x=x+dl+sl;
		}
		if(x<max_x){
			mc.moveTo(x,s_y);
			mc.lineTo(max_x,s_y);
		}		
	}
	
	static public  function get_Atrs(mc_padre,depth){ //Mail
		var Atrs=mc_padre.createEmptyMovieClip("Atrs",6+depth);
		var color=0x000066;
		Atrs.lineStyle(1,0x111111,100);
		drawFlashDashLine(Atrs,1.5,2,12,4,2);
		drawFlashDashLine(Atrs,1.5,8,12,4,2);
		drawFlashDashLine(Atrs,1.5,10,12,4,2);
		drawFlashDashLine(Atrs,1.5,12,12,4,2);
		Atrs.lineStyle(3,0x111166,100);
		drawFlashDashLine(Atrs,1,5,12,12,1);
		Atrs.lineStyle(1,0xeeeeff,100);
		drawFlashDashLine(Atrs,1,5,11,5,1);
		return Atrs;
	}

	static public  function get_kaddressbook(mc_padre,depth){ //Phone
		var phone=mc_padre.createEmptyMovieClip("phone",6+depth);
		var color=0xDDDBB9;
		phone.lineStyle(6,0x444444,100);
		phone.moveTo(3,11);
		phone.lineTo(4,10);
		phone.moveTo(10,4);
		phone.lineTo(11,3);
		phone.lineStyle(4,0x444444,100);
		phone.moveTo(5,12);
		phone.lineTo(12,5);

		return phone;
	}

	static public  function get_forward(mc_padre,depth){ //Forward
		var foward=mc_padre.createEmptyMovieClip("foward",6+depth);
		var color=0x00CECE;
		foward.lineStyle(1,0x555555,100);
		foward.beginFill(color,100);
		foward.moveTo(14,7);
		foward.lineTo(7,0);
		foward.lineTo(7,4);
		foward.lineTo(1,4);
		foward.lineTo(1,10);
		foward.lineTo(7,10);
		foward.lineTo(7,14);
		foward.lineTo(14,7);
		foward.endFill();
		foward.lineStyle(1,0x555555,1);
		foward.moveTo(0,14);
		foward.lineTo(0,13);

		return foward;
	}

	static public function get_attach(mc_padre,depth){ //Look here
		var attach=mc_padre.createEmptyMovieClip("attach",6+depth);
		attach.lineStyle(1,0x000000,100);
		attach.moveTo(6,1);
		attach.lineTo(14,9);
		attach.lineTo(14.5,14.2);
		attach.lineTo(9,14);
		attach.lineTo(1,5);
		attach.lineTo(4,2);
		attach.lineTo(11,9);
		attach.lineTo(9,11);
		attach.lineTo(4,6);
		attach.lineStyle(1,0xEEEEAA,100);
		attach.moveTo(7,0.5);
		attach.lineTo(14,8);
		attach.lineTo(13.5,13.5);
		attach.lineTo(9,13.5);
		attach.lineTo(1,4.5);
		attach.lineTo(4,1.5);
		attach.lineTo(11,8.5);
		attach.lineTo(9,10.5);
		attach.lineTo(4,5.5);
		return attach;
	}

    
	static public function get_mm_link(mc_padre,depth){
		var link=mc_padre.createEmptyMovieClip("link",6+depth);
		var color=0xEE0000;

		link.lineStyle(1,0x000000,100);
		link.moveTo(3.5,3.5);
		link.lineTo(11.5,11.5);
		link.moveTo(11,4.5);
		link.lineTo(4,11);

		link.lineStyle(3,0x000000,100);
		link.moveTo(2.5,2.5);
		link.lineTo(2.5,3.5);
		link.moveTo(10.5,3.5);
		link.lineTo(11.5,3.5);
		link.moveTo(3.5,11.5);
		link.lineTo(4.5,11.5);
		link.moveTo(10.5,11.5);
		link.lineTo(11.5,11.5);
		link.moveTo(6.5,8);
		link.lineTo(7.5,8);

		link.lineStyle(3,0xEE0000,100);
		link.moveTo(2,2);
		link.lineTo(2,3);
		link.lineStyle(3,0xEEEE00,100);
		link.moveTo(10,3);
		link.lineTo(11,3);
		link.lineStyle(3,0x0000EE,100);
		link.moveTo(3,11);
		link.lineTo(4,11);
		link.lineStyle(3,0x00EE00,100);
		link.moveTo(10,11);
		link.lineTo(11,11);

		link.lineStyle(3,0x00EEEE,100);
		link.moveTo(6.5,7.5);
		link.lineTo(7.5,7.5);

		return link;
	}

	static public function get_stop(mc_padre,depth){ //stop
		var stop=mc_padre.createEmptyMovieClip("stop",6+depth);
		stop.lineStyle(3,0x555555,100);
		stop.moveTo(6,3);
		stop.lineTo(6,12);
		stop.moveTo(7,3);
		stop.lineTo(7,12);
		stop.moveTo(8,3);
		stop.lineTo(8,12);
		stop.lineStyle(4,0xEE3333,100);
		stop.moveTo(6.8,5);
		stop.lineTo(7.2,5);
		stop.lineStyle(1,0xFFFFFF,100);
		stop.moveTo(7,5);
		stop.lineTo(7,5.1);
		stop.lineStyle(6,0xFF0000,30);
		stop.moveTo(6,5);
		stop.lineTo(8,5);
		return stop;
	}

	static public function get_messagebox_warning(mc_padre,depth){ //Important
		var important=mc_padre.createEmptyMovieClip("important",6+depth);

		var color=0xFEF232;
		var colorBorde=0xFDA600;
		var color2=0x777777;

		important.lineStyle(1,colorBorde,100);
		important.beginFill(color,100);
		important.moveTo(7,0);
		important.lineTo(14,14);
		important.lineTo(0,14);
		important.lineTo(7,0);
		important.endFill();
		important.lineStyle(2,color2,100);
		important.moveTo(7,12);
		important.lineTo(7,11);

		important.moveTo(7,8);
		important.lineTo(7,4);

		return important;
	}

	static public function get_bookmark(mc_padre,depth){ //Excelent
		var excelent=mc_padre.createEmptyMovieClip("excelent",6+depth);

		var color=0x27E6FE;
		var colorBorde=0xEC9500;
		var colors = [ 0xFFFF95, 0xFCA200];
		var alphas = [ 100, 100 ];
		var ratios = [ 0, 0xFF ];
		//matrix = { a:200, b:0, c:0, d:0, e:200, f:0, g:200, h:200, i:1 };
		var matrix = { matrixType:"box", x:0, y:0, w:14, h:14, r:(90/180)*Math.PI };
		excelent.lineStyle(1,colorBorde,100);
		excelent.beginGradientFill("linear",colors, alphas, ratios, matrix );

		excelent.moveTo(7,0);
		var incRad=(360/10)*Math.PI/180;
		var grade=(-90)*Math.PI/180;
		var short=true;
		for(var i=1;i<=10;i++){
			grade+=incRad;
			if(short)
				excelent.lineTo(7+3*Math.cos(grade),7+3*Math.sin(grade));
			else
				excelent.lineTo(7+8*Math.cos(grade),7+8*Math.sin(grade));
			short= short?false:true;
		}
		excelent.endFill();

		return excelent;
	}

	static public function get_wizard(mc_padre,depth){ //Magic
		var wizard=mc_padre.createEmptyMovieClip("wizard",6+depth);


		wizard.lineStyle(1,0x333333,100);
		wizard.beginFill(0xFDFD83,100);
		wizard.moveTo(10,0);
		var incRad=(360/10)*Math.PI/180;
		var grade=(-90)*Math.PI/180;
		var short=true;
		for(var i=1;i<=10;i++){
			grade+=incRad;
			if(short)
				wizard.lineTo(10+2*Math.cos(grade),4+2*Math.sin(grade));
			else
				wizard.lineTo(10+4*Math.cos(grade),4+4*Math.sin(grade));
			short= short?false:true;
		}
		wizard.endFill();

		wizard.lineStyle(3,0x222222,100);
		wizard.moveTo(1,13);
		wizard.lineTo(7,7);
		wizard.lineStyle(1,0x888888,100);
		wizard.moveTo(1,13);
		wizard.lineTo(7,7);

		return wizard;
	}

	static public function get_button_ok(mc_padre,depth){ //OK
		var ok=mc_padre.createEmptyMovieClip("ok",6+depth);
		var color=0x00FF00;
		var colorBorde=0x00AA00;
		ok.lineStyle(1,colorBorde,100);
		ok.beginFill(color,100);
		ok.moveTo(0,2);
		ok.lineTo(3,10);
		ok.lineTo(14,1);
		ok.lineTo(5,5);
		ok.lineTo(0,2);
		ok.endFill();
		ok._xscale=120;
		ok._yscale=120;
		return ok;
	}

	static public function get_button_cancel(mc_padre,depth){ //Not OK
		var cancel=mc_padre.createEmptyMovieClip("cancel",6+depth);
		var color=0xFF0000;
		var colorBorde=0xAA0000;
		cancel.lineStyle(1,colorBorde,100);
		cancel.beginFill(color,100);
		cancel.moveTo(0,2);
		cancel.lineTo(2,0);
		cancel.lineTo(7,5);
		cancel.lineTo(12,0);
		cancel.lineTo(14,2);
		cancel.lineTo(9,7);
		cancel.lineTo(14,12);
		cancel.lineTo(12,14);
		cancel.lineTo(7,9);
		cancel.lineTo(2,14);
		cancel.lineTo(0,12);
		cancel.lineTo(5,7);
		cancel.lineTo(0,2);
		cancel.endFill();
		return cancel;
	}
	
	static public function get_full_1(mc_padre,depth){
		return get_full(mc_padre,depth,"1",0x770077);
	}
	static public function get_full_2(mc_padre,depth){
		return get_full(mc_padre,depth,"2",0x928633);
	}
	static public function get_full_3(mc_padre,depth){
		return get_full(mc_padre,depth,"3",0x339192);
	}
	static public function get_full_4(mc_padre,depth){
		return get_full(mc_padre,depth,"4",0x007700);
	}
	static public function get_full_5(mc_padre,depth){
		return get_full(mc_padre,depth,"5",0x454C7F);
	}
	static public function get_full_6(mc_padre,depth){
		return get_full(mc_padre,depth,"6",0x992B2D);
	}
	static public function get_full_7(mc_padre,depth){
		return get_full(mc_padre,depth,"7",0x85982B);
	}
		static public  function get_full(mc_padre,depth,numero,color){ //Mail
		var Priority=mc_padre.createEmptyMovieClip("Mail",6+depth);
		Priority.lineStyle();
		//Priority.beginFill(0xFFFFFF,100);
		//Priority.drawSquare(1,1,16)
		//Priority.endFill();
		Priority.fillCircle(7,9,9,color);
		Priority.createTextField("texto",2,4,-1,16,18);
		Priority.texto.text=numero;
		Priority.texto.background=false;
		Priority.texto.selectable=false;
		var my_fmt:TextFormat = new TextFormat();
		my_fmt.color=0xFFFFFF;
		my_fmt.size=14;
		my_fmt.bold=true;
		Priority.texto.setTextFormat(my_fmt);
		
		return Priority;
	}

	
}
