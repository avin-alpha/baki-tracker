var root = 'mfe-pps:///';
var pages = [
        {'page':'breach', 'targets': [{'os':'default', 'redir':root+'identityBreach'} ]},
        {'page':'addDevice', 'targets': [{'os':'Win10', 'redir':'https://download.mcafee.com/molbin/iss-loc/ppsinstaller/redir.html'}, {'os':'Win10S', 'redir':'https://www.microsoft.com/en-us/p/mcafee-personal-security/9n1sqw2nkpds'}, {'os':'Android','redir':'https://play.google.com/store/apps/details?id=com.truekey.android'}, {'os':'iOS', 'redir':'https://apps.apple.com/us/app/true-key-by-mcafee/id932579221'}, {'os':'default', 'redir':'https://www.mcafee.com/'} ]}
    ];
var pageMap = new Map();

function make_page_map()
{
    for (let p of pages) {
        let targetMap = new Map();
        for (let t of p.targets) {
            targetMap.set(t.os, t.redir);
        }
        pageMap.set(p.page, targetMap);
    }
}

function get_page(page, os) {
    let p = pageMap.get(page);
    let t;
    if (!p) {
        return null;
    }
    t = p.get(os);
    if (!t) {
        t = p.get('default');
        if (!t) {
            return null;
        }
    }

    return t;
}


var queryParams = new URL(window.location.href).searchParams;

function get_param(key) {
	return (queryParams ? queryParams.get(key) : null);
}

function redirect(redir) {
    window.location.replace(redir);
    return true;
}

function detect(regex) {
	alert(navigator.userAgent.toLowerCase());
    return !!navigator.platform && regex.test(navigator.platform);
}

function on_windows_homepro(redir) {
    if (window.external.getHostEnvironmentValue)
    {
        let osmode = JSON.parse(window.external.getHostEnvironmentValue('os-mode'));
        if (!osmode || osmode["os-mode"] !== "0")
        {
            return false;
        }
    }
        
    if (detect(/Win32|Win64/))
    {
        return redirect(redir);
    }

    return false;
}

// this only works on Edge - as of 04-20-2020 there are no other major browsers in the MSFT store
function on_windows_10s(redir) {
    if (!window.external.getHostEnvironmentValue)
    {
        return false;
    }

    let osmode = JSON.parse(window.external.getHostEnvironmentValue('os-mode'));
    if (!osmode || osmode["os-mode"] != "2")
    {
        return false;
    }
        
    if (detect(/Win32|Win64/) && osmode.os-mode === "2")
    {
        return redirect(redir);
    }

    return false;
}

function on_android(redir) {
    if (detect(/android/)) {
	    alert("android detected");
        return redirect(redir);
    }

    return false;
}

function on_ios(redir) {
    if (detect(/iPad|iPhone|iPod/)) {
        return redirect(redir);
    }

    return false;
}

function on_other(redir) {
    return redirect(redir);
}

function on_load() {
    let p = get_param('t');
    if (!p) {
        return;
    }

    make_page_map();
    //if (on_windows_10s(get_page(p, "Win10S"))) return true;
    //if (on_windows_homepro(get_page(p, "Win10"))) return true;
    if (on_android(get_page(p, "Android"))) return true;
    if (on_ios(get_page(p, "iOS"))) return true;
    return on_other(get_page(p, "default"));
}
