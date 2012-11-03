// UIButton

UIButton.prototype._drawButton = function(w, h) {
    var l = 0, t = 0, r = w, b = h;

    var ctx = this._canvas.getContext('2d');
    ctx.save();

    ctx.clearRect(0, 0, w, h);
    ctx.fillStyle = 'ButtonFace'; //'rgb(212,208,200)';
    ctx.fillRect(0, 0, w, h);

    if (this._focus || this._pressed) {
        ctx.strokeStyle = 'black';
        ctx.strokeRect(0, 0, w, h);
        l++, t++, r--, b--;
    }

    if (this._pressed) {
        ctx.strokeStyle = 'white';
        ctx.beginPath();
        ctx.moveTo(r, t);
        ctx.lineTo(r, b);
        ctx.lineTo(l, b);
        ctx.stroke();

        ctx.strokeStyle = 'rgb(64,64,64)';
        ctx.beginPath();
        ctx.moveTo(l, b-1);
        ctx.lineTo(l, t);
        ctx.lineTo(r-1, t);
        ctx.stroke();

        ctx.strokeStyle = 'rgb(128,128,128)';
        ctx.beginPath();
        ctx.moveTo(l+1, b-2);
        ctx.lineTo(l+1, t+1);
        ctx.lineTo(r-2, t+1);
        ctx.stroke();
    } else {
        ctx.strokeStyle = 'white';
        ctx.beginPath();
        ctx.moveTo(l, b-1);
        ctx.lineTo(l, t);
        ctx.lineTo(r-1, t);
        ctx.stroke();

        ctx.strokeStyle = 'rgb(64,64,64)';
        ctx.beginPath();
        ctx.moveTo(r, t);
        ctx.lineTo(r, b);
        ctx.lineTo(l, b);
        ctx.stroke();

        ctx.strokeStyle = 'rgb(128,128,128)';
        ctx.beginPath();
        ctx.moveTo(l+1, b-1);
        ctx.lineTo(r-1, b-1);
        ctx.lineTo(r-1, t+1);
        ctx.stroke();
    }

    ctx.restore();
}

// UICookMenu classic skin properties
var UICookMenu_skinProperties =
{
  	// main menu display attributes
  	//
  	// Note.  When the menu bar is horizontal,
  	// mainFolderLeft and mainFolderRight are
  	// put in <span></span>.  When the menu
  	// bar is vertical, they would be put in
  	// a separate TD cell.

  	// HTML code to the left of the folder item
  	mainFolderLeft: '&nbsp;',
  	// HTML code to the right of the folder item
  	mainFolderRight: '&nbsp;',
	// HTML code to the left of the regular item
	mainItemLeft: '&nbsp;',
	// HTML code to the right of the regular item
	mainItemRight: '&nbsp;',

	// sub menu display attributes

	// HTML code to the left of the folder item
	folderLeft: '&nbsp;',
	// HTML code to the right of the folder item
	folderRight: '<img alt="" src="' + UIApplication.skinPath + 'image/arrow.gif">',
	// HTML code to the left of the regular item
	itemLeft: '&nbsp;',
	// HTML code to the right of the regular item
	itemRight: '&nbsp;',

	HSplit: '<td colspan="3"><hr/></td>',
	MainHSplit: '<td colspan="3"><hr/></td>',
	MainVSplit: '|'
};
