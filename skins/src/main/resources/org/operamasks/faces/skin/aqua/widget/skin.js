// UIButton

UIButton.prototype._BUTTON_HEIGHT = 25;
UIButton.prototype._CAPS_WIDTH = 13;
UIButton.prototype._BODY_WIDTH = 100;
UIButton.prototype._IMAGE_WIDTH = 100 + 13*2;
UIButton.prototype._IMAGE_HEIGHT = 25*4;

UIButton._buttonImage = null;
UIButton._buttonImageLoaded = false;

UIButton.prototype._getPreferredWidth = function(width) {
    if (!this._button.style.width)
        width += 10;
    return width;
}

UIButton.prototype._getPreferredHeight = function(height) {
    return this._BUTTON_HEIGHT;
}

UIButton.prototype._loadImage = function() {
    var img = UIButton._buttonImage;

    if (img == null) {
        img = new Image();
        img.onload = new Function("UIButton._buttonImageLoaded = true;");
        img.src = UIApplication.skinPath + "image/button.png";
        img.width = this._IMAGE_WIDTH;
        img.height = this._IMAGE_HEIGHT;
        UIButton._buttonImage = img;
    }

    if (!UIButton._buttonImageLoaded) {
        this._loadImageHandler = this.addEventListener(img, 'load', this.drawButton);
        return null;
    } else if (this._loadImageHandler) {
        this.removeEventListener(img, 'load', this._loadImageHandler);
        this._loadImageHandler = null;
    }

    return img;
}

UIButton.prototype._drawButton = function(width, height) {
    var img = this._loadImage();
    if (img == null) return;

    var image_width = this._IMAGE_WIDTH;

    var ctx = this._canvas.getContext('2d');
    ctx.clearRect(0, 0, width, height);

    var offset_y = 0;
    if (this._button.disabled) {
        offset_y = this._BUTTON_HEIGHT*3;
    } else if (this._pressed) {
        offset_y = this._BUTTON_HEIGHT;
    } else if (this._focus || this._hover) {
        offset_y = this._BUTTON_HEIGHT*2;
    }

    if (width < this._CAPS_WIDTH*2) {
        var startcaps_width = Math.floor(width / 2);
        var endcaps_width = width - startcaps_width;
        ctx.drawImage(img, 0, offset_y, startcaps_width, height, 0, 0, startcaps_width, height);
        ctx.drawImage(img, image_width - endcaps_width, offset_y, endcaps_width, height, startcaps_width, 0, endcaps_width, height);
    } else {
        ctx.drawImage(img, 0, offset_y, this._CAPS_WIDTH, height, 0, 0, this._CAPS_WIDTH, height);

        if (width - this._CAPS_WIDTH <= this._BODY_WIDTH + this._CAPS_WIDTH) {
            ctx.drawImage(img, image_width - width + this._CAPS_WIDTH, offset_y,
                          width - this._CAPS_WIDTH, height,
                          this._CAPS_WIDTH, 0, width - this._CAPS_WIDTH, height);
        } else {
            var body_width = width - this._CAPS_WIDTH*2;
            var offset_x = this._CAPS_WIDTH;
            while (body_width > 0) {
                var chunk_width = Math.min(body_width, this._BODY_WIDTH);
                ctx.drawImage(img, this._CAPS_WIDTH, offset_y,
                              chunk_width, height, offset_x, 0, chunk_width, height);
                body_width -= this._BODY_WIDTH;
                offset_x += this._BODY_WIDTH;
            }
            ctx.drawImage(img, this._CAPS_WIDTH + this._BODY_WIDTH, offset_y,
                          this._CAPS_WIDTH, height, width - this._CAPS_WIDTH, 0,
                          this._CAPS_WIDTH, height);
        }
    }
}

// UIWindow customization

UIWindow.prototype.MIN_CAPTION_HEIGHT = 23;
UIWindow.prototype.MIN_WIDTH = 124;

UIWindow.prototype.CAPTION_MARGIN_LEFT = 0;
UIWindow.prototype.CAPTION_MARGIN_RIGHT = 0;
UIWindow.prototype.CAPTION_MARGIN_TOP = 0;
UIWindow.prototype.CAPTION_PADDING_TOP = 0;
UIWindow.prototype.CAPTION_PADDING_BOTTOM = 0;
UIWindow.prototype.ICON_MARGIN_RIGHT = 4;
UIWindow.prototype.CONTENT_MARGIN_LEFT = 1;
UIWindow.prototype.CONTENT_MARGIN_RIGHT = 1;
UIWindow.prototype.CONTENT_MARGIN_BOTTOM = 16;

UIWindow.prototype._createWindowHook = function() {
    this._buttonPane.insertBefore(this._closeButton, this._minimizeButton);

    // create shadow pane
    this._shadowPane = document.createElement('div');
    this._shadowPane.className = 'window-shadow';
    this._window.insertBefore(this._shadowPane, this._window.firstChild);

    this._shadow_tl = document.createElement('div');
    this._shadow_tl.className = 'window-shadow-tl';
    this._shadowPane.appendChild(this._shadow_tl);

    this._shadow_tc = document.createElement('div');
    this._shadow_tc.className = 'window-shadow-tc';
    this._shadowPane.appendChild(this._shadow_tc);

    this._shadow_tr = document.createElement('div');
    this._shadow_tr.className = 'window-shadow-tr';
    this._shadowPane.appendChild(this._shadow_tr);

    this._shadow_cl = document.createElement('div');
    this._shadow_cl.className = 'window-shadow-cl';
    this._shadowPane.appendChild(this._shadow_cl);

    this._shadow_cr = document.createElement('div');
    this._shadow_cr.className = 'window-shadow-cr';
    this._shadowPane.appendChild(this._shadow_cr);
    
    this._shadow_bl = document.createElement('div');
    this._shadow_bl.className = 'window-shadow-bl';
    this._shadowPane.appendChild(this._shadow_bl);

    this._shadow_bc = document.createElement('div');
    this._shadow_bc.className = 'window-shadow-bc';
    this._shadowPane.appendChild(this._shadow_bc);

    this._shadow_br = document.createElement('div');
    this._shadow_br.className = 'window-shadow-br';
    this._shadowPane.appendChild(this._shadow_br);
}


UIWindow.prototype._layoutChildrenHook = function() {
    if (UIWidget.ie) { // IE fix
        var width = this._window.clientWidth;
        var height = this._window.clientHeight;
        this._shadowPane.style.width = (width+30) + "px";
        this._shadowPane.style.height = (height+30) + "px";
        this._shadow_tc.style.width = (width-18) + "px";
        this._shadow_bc.style.width = (width-30) + "px";
        this._shadow_cl.style.height = (height-39) + "px";
        this._shadow_cr.style.height = (height-39) + "px";
    }

    if (this._state == 'normal' && /UIDialog/.test(this._window.className)) {
        UIWidget.setBottom(this._clientPane, 0);
        if (UIWidget.ie) {
            UIWidget.setOffsetHeight(this._clientPane, this._window.clientHeight - this._caption.offsetHeight);
            UIWidget.setOffsetHeight(this._contentPane, this._clientPane.clientHeight);
        }
    }
}

UIWindow.prototype._onCaptionMouseOver = function(e) {
    switch (e.target) {
      case this._buttonPane:
      case this._minimizeButton:
      case this._maximizeButton:
      case this._closeButton:
        this._setButtonHover(this._minimizeButton, true);
        this._setButtonHover(this._maximizeButton, true);
        this._setButtonHover(this._closeButton, true);
        break;
    }
}

UIWindow.prototype._onCaptionMouseOut = function(e) {
    switch (e.target) {
      case this._buttonPane:
      case this._minimizeButton:
      case this._maximizeButton:
      case this._closeButton:
        this._setButtonHover(this._minimizeButton, false);
        this._setButtonHover(this._maximizeButton, false);
        this._setButtonHover(this._closeButton, false);
        break;
    }
}

// UICookMenu customization

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

	HSplit: '<td colspan="3"><div class="MenuSplit"></div></td>',
	MainHSplit: '<td colspan="3"><div class="MenuSplit"></div></td>',
	MainVSplit: '|'
};
