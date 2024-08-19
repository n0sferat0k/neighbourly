var HL;

function init_ABC_hash_list(id)
{
    var obj = document.getElementById(id);
    HL = new ABC_hashlist(obj);
}

//THE HASHLIST ITSELF WITH OPTIONS FOR ADDING NEW ELEMENTS
function ABC_hashlist(obj)
{
	this.groups = new Array();
	this.obj = obj;
	
	this.addElement = function (element)
	{	
	 	var i,j;
	 	var placed = false;
	 	var hlg;
		for(i=0;i<this.groups.length;i++)
		{
			if(this.groups[i].addElement(element))
			{
				placed = true;
			}
		}
		if(!placed)
		{			 	
			hlg = new ABC_hashlist_group(element);
			this.sortInsert(hlg);
		}
	}	
	
	this.sortInsert = function (hlg)
	{
	 	var inserted = false;
	 	var i;
		for(i=0;i<this.groups.length;i++)
		{
			if(this.groups[i].key > hlg.key)
			{
				for(j=this.groups.length;j>i;j--)
				{
					this.groups[j]=this.groups[j-1];
				}
				this.groups[i]=hlg;
				inserted = true;
				break;
			}
		}
		if(!inserted)
		{
			this.groups[this.groups.length]=hlg;
		}
	}
	
	this.dumpContent = function()
	{
	 	var i;		 
		for(i=0;i<this.groups.length;i++)
		{
		 	this.groups[i].dumpContent(this.obj);
		}		
	}	
	this.liftContent = function()
	{
		var i;		 
		for(i=0;i<this.groups.length;i++)
		{
		 	this.groups[i].liftContent();
		}
	}
	this.clearContent = function()
	{
		var i;		 
		this.liftContent();
		for(i=0;i<this.groups.length;i++)
		{
		 	this.groups[i].clearContent();
		 	delete this.groups[i];
		}	
		delete this.groups;
		this.groups = new Array();
	}
	this.filterContent = function(filter)
	{
	 	var i;
		for(i=0;i<this.groups.length;i++)
		{
		 	this.groups[i].filterContent(filter);
		}
	}	
}	
//A GROUP IN THE HASHLIST DENOTED BY A CHARACTER OR A LIST OF CHARACTERS	
function ABC_hashlist_group(el)
{
	this.key = el.getHashKey();
	this.elements = new Array(el);
	this.obj = document.createElement('DIV');
	
	this.obj.innerHTML = '<div class="ABC_hash_list_group_lable">' + this.key + '</div>';
	this.obj.className = 'ABC_hash_list_group';
			
	this.addElement = function(el)
	{
	 	var i,j;
	 	var inserted = false;
		var k = el.getHashKey();
		if(k == this.key)
		{
			for(i=0;i<this.elements.length;i++)
			{
				if(this.elements[i].text > el.text)
				{
					for(j=this.elements.length;j>i;j--)
					{
						this.elements[j]=this.elements[j-1];
					}
					this.elements[i]=el;
					inserted = true;
					break;
				}
			}
			if(!inserted)
			{
				this.elements[this.elements.length]=el;
			}
			return true;
		}
		else
		{
			return false;
		}
	}
	this.dumpContent = function(o)
	{
	 	var i;					 	
		for(i=0;i<this.elements.length;i++)
		{
		 	this.elements[i].dumpContent(this.obj);			 	
		}
		o.appendChild(this.obj);
	}
	this.liftContent = function()
	{
		var i;		 
		if(this.obj.parentNode)
			this.obj.parentNode.removeChild(this.obj);
		for(i=0;i<this.elements.length;i++)
		{
		 	this.elements[i].liftContent();
		}
	}
	this.clearContent = function()
	{
		var i;		 
		for(i=0;i<this.elements.length;i++)
		{
		 	this.elements[i].clearContent();
		 	delete this.elements[i];
		}
		delete this.obj;
		delete this.elements;
	}
	this.filterContent = function(filter)
	{
	 	var count=0;
	 	var i = 0;
	 	
		for(i=0;i<this.elements.length;i++)
		{	
			
			this.elements[i].filterContent(filter);			 	
			if(this.elements[i].status == 1)
		 	{
				count++;
			}												
		}
		
		if(count==0)
		{
			this.obj.style.display='none';
		}
		else
		{
			this.obj.style.display='block';
		}
	}		
}
//A SINGLE ENTRY IN THE HASHLIST 
function ABC_hashlist_element(text,obj)
{
	this.text = text;
	this.obj = obj;
	this.status = 1;
	
	this.getHashKey = function()
	{	 	
		return this.text.charAt(0).toUpperCase();		
	}
	this.dumpContent = function(o)
	{			
		o.appendChild(this.obj);
	}
	this.liftContent = function()
	{
		if(this.obj.parentNode)
			this.obj.parentNode.removeChild(this.obj);
	}
	this.clearContent = function()
	{
		delete this.obj;
	}
	this.filterContent = function(filter)
	{
	 	if(filter == "")
	 	{
			this.obj.style.display='block';
			this.status=1;	
		}
		else
		{
			if(this.text.toLowerCase().indexOf(filter.toLowerCase()) == -1)
			{				 	
				this.obj.style.display='none';	
				this.status=0;
			}
			else
			{			 
				this.obj.style.display='block';
				this.status=1;
			}
		}
	}		
}

function add_element_to_ABC_hash_list(id, name, obj_id)
{
 	var obj = document.getElementById(obj_id);	
	obj.style.display = "block";	 
	var he = new ABC_hashlist_element(name,obj);
	HL.addElement(he);		
}	