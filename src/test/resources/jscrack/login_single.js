(function(e, t) {
    typeof module == "object" && typeof module.exports == "object" ? module.exports = e.document ? t(e, !0) : function(e) {
        if (!e.document)
            throw new Error("Avalon requires a window with a document");
        return t(e)
    }
        : t(e)
})(typeof window != "undefined" ? window : this, function(e, t) {
    function u() {
        e.console && avalon.config.debug && Function.apply.call(console.log, console, arguments)
    }
    function L() {}
    function A(e, t) {
        typeof e == "string" && (e = e.match(p) || []);
        var n = {}
            , r = t !== void 0 ? t : 1;
        for (var i = 0, s = e.length; i < s; i++)
            n[e[i]] = r;
        return n
    }
    function M() {
        if (e.VBArray) {
            var t = document.documentMode;
            return t ? t : e.XMLHttpRequest ? 7 : 6
        }
        return NaN
    }
    function P(e) {
        return m.test(b.call(e))
    }
    function j(e, t) {
        return e = Math.floor(e) || 0,
            e < 0 ? Math.max(t + e, 0) : Math.min(e, t)
    }
    function q(e) {
        if (!e)
            return !1;
        var t = e.length;
        if (t === t >>> 0) {
            var n = b.call(e).slice(8, -1);
            if (/(?:regexp|string|function|window|global)$/i.test(n))
                return !1;
            if (n === "Array")
                return !0;
            try {
                return {}.propertyIsEnumerable.call(e, "length") === !1 ? /^\s?function/.test(e.item || e.callee) : !0
            } catch (r) {
                return !e.window
            }
        }
        return !1
    }
    function $(e, t, n) {
        var r = "for(var " + e + "i=0,n = this.length; i < n; i++){" + t.replace("_", "((i in this) && fn.call(scope,this[i],i,this))") + "}" + n;
        return Function("fn,scope", r)
    }
    function J(e, t) {
        try {
            while (t = t.parentNode)
                if (t === e)
                    return !0;
            return !1
        } catch (n) {
            return !1
        }
    }
    function K() {
        return (new XMLSerializer).serializeToString(this)
    }
    function et(e) {
        var t = {};
        for (var n in e)
            t[n] = e[n];
        var i = t.target = e.srcElement;
        if (e.type.indexOf("key") === 0)
            t.which = e.charCode != null ? e.charCode : e.keyCode;
        else if (Z.test(e.type)) {
            var s = i.ownerDocument || r
                , o = s.compatMode === "BackCompat" ? s.body : s.documentElement;
            t.pageX = e.clientX + (o.scrollLeft >> 0) - (o.clientLeft >> 0),
                t.pageY = e.clientY + (o.scrollTop >> 0) - (o.clientTop >> 0),
                t.wheelDeltaY = t.wheelDelta,
                t.wheelDeltaX = 0
        }
        return t.timeStamp = new Date - 0,
            t.originalEvent = e,
            t.preventDefault = function() {
                e.returnValue = !1
            }
            ,
            t.stopPropagation = function() {
                e.cancelBubble = !0
            }
            ,
            t
    }
    function it(e) {
        for (var t in e) {
            if (!y.call(e, t))
                continue;
            var n = e[t];
            typeof it.plugins[t] == "function" ? it.plugins[t](n) : typeof it[t] == "object" ? avalon.mix(it[t], n) : it[t] = n
        }
        return this
    }
    function ct(e) {
        return (e + "").replace(lt, "\\$&")
    }
    function St(e, t, n) {
        if (Array.isArray(e)) {
            var r = e.concat();
            e.length = 0;
            var i = Bt(e);
            return i.pushArray(r),
                i
        }
        if (!e || e.nodeType > 0 || e.$id && e.$events)
            return e;
        var s = Array.isArray(e.$skipArray) ? e.$skipArray : [];
        s.$special = t || {};
        var o = {};
        n = n || {};
        var u = {}
            , a = {}
            , f = [];
        gt.forEach(function(t) {
            delete e[t]
        });
        var l = Object.keys(e);
        l.forEach(function(t, r) {
            var i = e[t];
            n[t] = i;
            if (Mt(t, i, s)) {
                u[t] = [];
                var o = avalon.type(i);
                o === "object" && D(i.get) && Object.keys(i).length <= 2 ? (r = Nt(t, i),
                    f.push(r)) : d.test(o) ? r = Ct(t, i, o, u[t], n) : r = Tt(t, i),
                    a[t] = r
            }
        }),
            o = wt(o, Dt(a), e);
        for (var c = 0; c < l.length; c++) {
            var h = l[c];
            a[h] || (o[h] = e[h])
        }
        xt(o, "$id", O()),
            xt(o, "$model", n),
            xt(o, "$events", u),
            bt ? xt(o, "hasOwnProperty", function(e) {
                return e in o.$model
            }) : o.hasOwnProperty = function(e) {
                return e in o.$model && e !== "hasOwnProperty"
            }
        ;
        for (c in vt)
            xt(o, c, vt[c].bind(o));
        return o.$reinitialize = function() {
            f.forEach(function(e) {
                delete e._value,
                    delete e.oldArgs,
                    e.digest = function() {
                        e.call(o)
                    }
                    ,
                    Ut.begin({
                        callback: function(t, n) {
                            var r = n._name;
                            if (n !== e) {
                                var i = t.$events[r];
                                Wt(i, e.digest)
                            }
                        }
                    });
                try {
                    e.get.call(o)
                } finally {
                    Ut.end()
                }
            })
        }
            ,
            o.$reinitialize(),
            o
    }
    function xt(e, t, n) {
        bt ? Object.defineProperty(e, t, {
            value: n,
            writable: !0,
            enumerable: !1,
            configurable: !0
        }) : e[t] = n
    }
    function Tt(e, t) {
        function n(e) {
            var t = n._value;
            return arguments.length > 0 ? (!h && !Ot(e, t) && (n.updateValue(this, e),
                n.notify(this, e, t)),
                this) : (Ut.collectDependency(this, n),
                t)
        }
        return At(n, e),
            n._value = t,
            n
    }
    function Nt(e, t) {
        function n(t) {
            var r = n._value
                , i = "_value"in n;
            if (arguments.length > 0) {
                if (h)
                    return this;
                if (typeof n.set == "function" && n.oldArgs !== t) {
                    n.oldArgs = t;
                    var s = this.$events
                        , o = s[e];
                    s[e] = [],
                        n.set.call(this, t),
                        s[e] = o,
                        t = n.get.call(this),
                    t !== r && (n.updateValue(this, t),
                        n.notify(this, t, r))
                }
                return this
            }
            return t = n.get.call(this),
                n.updateValue(this, t),
            i && r !== t && n.notify(this, t, r),
                t
        }
        return n.set = t.set,
            n.get = t.get,
            At(n, e),
            n
    }
    function Ct(e, t, n, r, i) {
        function s(t) {
            var r = s._value
                , i = s._vmodel;
            if (arguments.length > 0) {
                if (h)
                    return this;
                if (n === "array") {
                    var o = i
                        , u = t
                        , f = o.length
                        , l = u.length;
                    o.$lock = !0,
                        f > l ? o.splice(l, f - l) : l > f && o.push.apply(o, u.slice(f));
                    var c = Math.min(f, l);
                    for (var p = 0; p < c; p++)
                        o.set(p, u[p]);
                    delete o.$lock,
                        o._fire("set")
                } else if (n === "object") {
                    var d = this.$events[e] || []
                        , v = avalon.mix(!0, {}, t);
                    for (p in i)
                        i.hasOwnProperty(p) && y.call(v, p) && (i[p] = v[p]);
                    i = s._vmodel = St(t),
                        i.$events[a] = d,
                    d.length && d.forEach(function(e) {
                        e.rollback && e.rollback(),
                            F[e.type](e, e.vmodels)
                    })
                }
                return s.updateValue(this, i.$model),
                    s.notify(this, this._value, r),
                    this
            }
            return Ut.collectDependency(this, s),
                i
        }
        At(s, e),
            Array.isArray(t) ? i[e] = t : i[e] = i[e] || {};
        var o = s._vmodel = St(t, 0, i[e]);
        return o.$events[a] = r,
            s
    }
    function kt(e, t) {
        e.$model[this._name] = this._value = t
    }
    function Lt(e, t, n) {
        var r = this._name
            , i = e.$events[r];
        i && (Xt(i),
            vt.$fire.call(e, r, t, n))
    }
    function At(e, t) {
        e._name = t,
            e.updateValue = kt,
            e.notify = Lt
    }
    function Mt(e, t, n) {
        if (D(t) || t && t.nodeType)
            return !1;
        if (n.indexOf(e) !== -1)
            return !1;
        var r = n.$special;
        return e && e.charAt(0) === "$" && !r[e] ? !1 : !0
    }
    function _t(e) {
        var t = Object.keys(e.$model ? e.$model : e);
        for (var n = 0; n < gt.length; n++) {
            var r = t.indexOf(gt[n]);
            r !== -1 && t.splice(r, 1)
        }
        return t
    }
    function Bt(e) {
        var t = [];
        t.$id = O(),
            t.$model = e,
            t.$events = {},
            t.$events[a] = [],
            t._ = St({
                length: e.length
            }),
            t._.$watch("length", function(e, n) {
                t.$fire("length", e, n)
            });
        for (var n in vt)
            t[n] = vt[n];
        return avalon.mix(t, It),
            t
    }
    function jt(e, t, n, r, i, s, o) {
        var u = this.length
            , a = 2;
        while (--a) {
            switch (e) {
                case "add":
                    var f = this.$model.slice(t, t + n).map(function(e) {
                        return d.test(avalon.type(e)) ? e.$id ? e : St(e, 0, e) : e
                    });
                    Ft.apply(this, [t, 0].concat(f)),
                        this._fire("add", t, n);
                    break;
                case "del":
                    var l = this._splice(t, n);
                    this._fire("del", t, n)
            }
            i && (e = i,
                t = s,
                n = o,
                a = 2,
                i = 0)
        }
        return this._fire("index", r),
        this.length !== u && (this._.length = this.length),
            l
    }
    function qt(e, t) {
        var n = e.length - 1;
        for (var r; r = e[t]; t++)
            r.$index = t,
                r.$first = t === 0,
                r.$last = t === n
    }
    function Rt(e, t) {
        var n = {};
        for (var r = 0, i = t.length; r < i; r++) {
            n[r] = e[r];
            var s = t[r];
            s in n ? (e[r] = n[s],
                delete n[s]) : e[r] = e[s]
        }
    }
    function Wt(e, t) {
        if (t.oneTime)
            return;
        e && avalon.Array.ensure(e, t) && t.element && Zt(t, e)
    }
    function Xt(e) {
        if (e && e.length) {
            new Date - Jt > 444 && typeof e[0] == "object" && en();
            var t = E.call(arguments, 1);
            for (var n = e.length, r; r = e[--n]; ) {
                var i = r.element;
                if (i && i.parentNode)
                    try {
                        var s = r.evaluator;
                        if (r.$repeat)
                            r.handler.apply(r, t);
                        else if ("$repeat"in r || !s)
                            F[r.type](r, r.vmodels);
                        else if (r.type !== "on") {
                            var o = s.apply(0, r.args || []);
                            r.handler(o, i, r)
                        }
                    } catch (u) {
                        console.log(u)
                    }
            }
        }
    }
    function Gt(e, t) {
        return !e.uuid && !t && (e.uuid = ++Vt,
            Qt[e.uuid] = e),
            e.uuid
    }
    function Yt(e) {
        return Qt[e]
    }
    function Zt(e, t) {
        var n = e.element;
        e.uuid || (n.nodeType !== 1 ? e.uuid = e.type + (e.pos || 0) + "-" + Gt(n.parentNode) : e.uuid = e.name + "-" + Gt(n));
        var r = e.lists || (e.lists = []);
        avalon.Array.ensure(r, t),
            t.$uuid = t.$uuid || O(),
        $t[e.uuid] || ($t[e.uuid] = 1,
            $t.push(e))
    }
    function en(e) {
        if (avalon.optimize)
            return;
        var t = $t.length
            , n = t
            , r = []
            , i = {}
            , s = {};
        while (e = $t[--t]) {
            var o = e.type;
            s[o] ? s[o]++ : (s[o] = 1,
                r.push(o))
        }
        var u = !1;
        r.forEach(function(e) {
            Kt[e] !== s[e] && (i[e] = 1,
                u = !0)
        }),
            t = n;
        if (u)
            while (e = $t[--t]) {
                if (!e.element)
                    continue;
                if (i[e.type] && nn(e.element)) {
                    $t.splice(t, 1),
                        delete $t[e.uuid],
                        delete Qt[e.element.uuid];
                    var a = e.lists;
                    for (var f = 0, l; l = a[f++]; )
                        avalon.Array.remove(a, l),
                            avalon.Array.remove(l, e);
                    tn(e)
                }
            }
        Kt = s,
            Jt = new Date
    }
    function tn(e) {
        e.element = null,
        e.rollback && e.rollback();
        for (var t in e)
            e[t] = null
    }
    function nn(e) {
        try {
            if (!e.parentNode)
                return !0
        } catch (t) {
            return !0
        }
        return e.msRetain ? 0 : e.nodeType === 1 ? !T.contains(e) : !avalon.contains(T, e)
    }
    function hn(e) {
        var t = e.nodeName;
        return t.toLowerCase() === t && e.scopeName && e.outerText === ""
    }
    function pn(e) {
        e.currentStyle.behavior !== "url(#default#VML)" && (e.style.behavior = "url(#default#VML)",
            e.style.display = "inline-block",
            e.style.zoom = 1)
    }
    function dn(e) {
        return e.replace(/([a-z\d])([A-Z]+)/g, "$1-$2").toLowerCase()
    }
    function vn(e) {
        return !e || e.indexOf("-") < 0 && e.indexOf("_") < 0 ? e : e.replace(/[-_][^-_]/g, function(e) {
            return e.charAt(1).toUpperCase()
        })
    }
    function gn(e) {
        if (!("classList"in e)) {
            e.classList = {
                node: e
            };
            for (var t in mn)
                e.classList[t.slice(1)] = mn[t]
        }
        return e.classList
    }
    function yn(e) {
        try {
            if (typeof e == "object")
                return e;
            e = e === "true" ? !0 : e === "false" ? !1 : e === "null" ? null : +e + "" === e ? +e : bn.test(e) ? avalon.parseJSON(e) : e
        } catch (t) {}
        return e
    }
    function Tn(e) {
        return e.window && e.document ? e : e.nodeType === 9 ? e.defaultView || e.parentWindow : !1
    }
    function Bn(e, t) {
        if (e.offsetWidth <= 0) {
            if (Hn.test(Nn["@:get"](e, "display"))) {
                var n = {
                    node: e
                };
                for (var r in Pn)
                    n[r] = e.style[r],
                        e.style[r] = Pn[r];
                t.push(n)
            }
            var i = e.parentNode;
            i && i.nodeType === 1 && Bn(i, t)
        }
    }
    function jn(e) {
        var t = e.tagName.toLowerCase();
        return t === "input" && /checkbox|radio/.test(e.type) ? "checked" : t
    }
    function Qn(e, t, n, r) {
        var i = []
            , s = " = " + n + ".";
        for (var o = e.length, u; u = e[--o]; )
            t.hasOwnProperty(u) && (i.push(u + s + u),
                r.vars.push(u),
            r.type === "duplex" && (e.get = n + "." + u),
                e.splice(o, 1));
        return i
    }
    function Gn(e) {
        var t = []
            , n = {};
        for (var r = 0; r < e.length; r++) {
            var i = e[r]
                , s = i && typeof i.$id == "string" ? i.$id : i;
            n[s] || (n[s] = t.push(i))
        }
        return t
    }
    function or(e, t) {
        return t = t.replace(tr, "").replace(nr, function() {
                return "],|"
            }).replace(rr, function(e, t) {
                return "[" + Rn(t)
            }).replace(ir, function() {
                return '"],["'
            }).replace(sr, function() {
                return '",'
            }) + "]",
        "return avalon.filters.$filter(" + e + ", " + t + ")"
    }
    function ur(e, t, r) {
        var i = r.type
            , s = r.filters || ""
            , o = t.map(function(e) {
                return String(e.$id).replace(er, "$1")
            }) + e + i + s
            , a = Kn(e).concat()
            , f = []
            , l = []
            , c = []
            , h = "";
        t = Gn(t),
            r.vars = [];
        for (var p = 0, d = t.length; p < d; p++)
            if (a.length) {
                var v = "vm" + n + "_" + p;
                l.push(v),
                    c.push(t[p]),
                    f.push.apply(f, Qn(a, t[p], v, r))
            }
        if (!f.length && i === "duplex")
            return;
        i !== "duplex" && (e.indexOf("||") > -1 || e.indexOf("&&") > -1) && r.vars.forEach(function(t) {
            var n = new RegExp("\\b" + t + "(?:\\.\\w+|\\[\\w+\\])+","ig");
            e = e.replace(n, function(n, r) {
                var i = n.charAt(t.length)
                    , s = e.slice(r + n.length)
                    , o = /^\s*\(/.test(s);
                if (i === "." || i === "[" || o) {
                    var u = "var" + String(Math.random()).replace(/^0\./, "");
                    if (o) {
                        var a = n.split(".");
                        if (a.length > 2) {
                            var l = a.pop();
                            return f.push(u + " = " + a.join(".")),
                            u + "." + l
                        }
                        return n
                    }
                    return f.push(u + " = " + n),
                        u
                }
                return n
            })
        }),
            r.args = c,
            delete r.vars;
        var m = Yn.get(o);
        if (m) {
            r.evaluator = m;
            return
        }
        h = f.join(", "),
        h && (h = "var " + h);
        if (/\S/.test(s)) {
            if (!/text|html/.test(r.type))
                throw Error("ms-" + r.type + "不支持过滤器");
            e = "\nvar ret" + n + " = " + e + ";\r\n",
                e += or("ret" + n, s)
        } else {
            if (i === "duplex") {
                var g = "\nreturn function(vvv){\n	" + h + ";\n	if(!arguments.length){\n		return " + e + "\n	}\n	" + (Zn.test(e) ? e : a.get) + "= vvv;\n} ";
                try {
                    m = Function.apply(L, l.concat(g)),
                        r.evaluator = Yn.put(o, m)
                } catch (y) {
                    u("debug: parse error," + y.message)
                }
                return
            }
            if (i === "on") {
                e.indexOf("(") === -1 ? e += ".call(this, $event)" : e = e.replace("(", ".call(this,"),
                    l.push("$event"),
                    e = "\nreturn " + e + ";";
                var b = e.lastIndexOf("\nreturn")
                    , w = e.slice(0, b)
                    , E = e.slice(b);
                e = w + "\n" + E
            } else
                e = "\nreturn " + e + ";"
        }
        try {
            m = Function.apply(L, l.concat("\n" + h + e)),
                r.evaluator = Yn.put(o, m)
        } catch (y) {
            u("debug: parse error," + y.message)
        } finally {
            a = f = l = null
        }
    }
    function ar(e) {
        var t = ut.test(e);
        if (t) {
            var n = qr(e);
            return n.length === 1 ? n[0].value : n.map(function(e) {
                return e.expr ? "(" + e.value + ")" : Rn(e.value)
            }).join(" + ")
        }
        return e
    }
    function fr(e, t, n, r) {
        e = e || "",
            ur(e, t, n),
        n.evaluator && !r && (n.handler = I[n.handlerName || n.type],
            avalon.injectBinding(n))
    }
    function cr(e, t, n) {
        var r = setTimeout(function() {
            var i = e.innerHTML;
            clearTimeout(r),
                i === n ? t() : cr(e, t, i)
        })
    }
    function hr(e, t) {
        var n = e.getAttribute("avalonctrl") || t.$id;
        e.setAttribute("avalonctrl", n),
            t.$events.expr = e.tagName + '[avalonctrl="' + n + '"]'
    }
    function dr(e, t) {
        for (var n = 0, r; r = e[n++]; )
            r.vmodels = t,
                F[r.type](r, t),
            r.evaluator && r.element && r.element.nodeType === 1 && r.element.removeAttribute(r.name);
        e.length = 0
    }
    function Er(e, t) {
        return e.priority - t.priority
    }
    function Sr(e, t, n) {
        var r = !0;
        if (t.length) {
            var i = Or ? Or(e) : e.attributes
                , s = []
                , o = []
                , a = {};
            for (var f = 0, l; l = i[f++]; )
                if (l.specified)
                    if (n = l.name.match(gr)) {
                        var c = n[1]
                            , h = n[2] || ""
                            , p = l.value
                            , d = l.name;
                        br[c] ? (h = c,
                            c = "on") : wr[c] && (c === "enabled" && (u("warning!ms-enabled或ms-attr-enabled已经被废弃"),
                            c = "disabled",
                            p = "!(" + p + ")"),
                            h = c,
                            c = "attr",
                            d = "ms-" + c + "-" + h,
                            o.push([l.name, d, p])),
                            a[d] = p;
                        if (typeof F[c] == "function") {
                            var v = p.replace(mr, "")
                                , m = p !== v
                                , g = {
                                type: c,
                                param: h,
                                element: e,
                                name: d,
                                value: v,
                                oneTime: m,
                                uuid: d + "-" + Gt(e),
                                priority: (yr[c] || c.charCodeAt(0) * 10) + (Number(h.replace(/\D/g, "")) || 0)
                            };
                            if (c === "html" || c === "text") {
                                var y = Ir(p);
                                avalon.mix(g, y),
                                    g.filters = g.filters.replace(Pr, function() {
                                        return g.type = "html",
                                            g.group = 1,
                                            ""
                                    })
                            } else if (c === "duplex")
                                var b = d;
                            else
                                d === "ms-if-loop" && (g.priority += 100);
                            s.push(g),
                            c === "widget" && (e.msData = e.msData || a)
                        }
                    }
            if (s.length) {
                s.sort(Er),
                    o.forEach(function(t) {
                        u("warning!请改用" + t[1] + "代替" + t[0] + "!"),
                            e.removeAttribute(t[0]),
                            e.setAttribute(t[1], t[2])
                    }),
                b && (a["ms-attr-checked"] && u("warning!一个控件不能同时定义ms-attr-checked与" + b),
                a["ms-attr-value"] && u("warning!一个控件不能同时定义ms-attr-value与" + b));
                for (f = 0; g = s[f]; f++) {
                    c = g.type;
                    if (xr.test(c))
                        return dr(s.slice(0, f + 1), t);
                    r && (r = !Tr.test(c))
                }
                dr(s, t)
            }
        }
        r && !lr[e.tagName] && ft.test(e.innerHTML.replace(Br, "<").replace(jr, ">")) && (vr && vr(e),
            Mr(e, t))
    }
    function Mr(e, t) {
        var n = avalon.slice(e.childNodes);
        _r(n, t)
    }
    function _r(e, t) {
        for (var n = 0, r; r = e[n++]; )
            switch (r.nodeType) {
                case 1:
                    Dr(r, t),
                    r.msCallback && (r.msCallback(),
                        r.msCallback = void 0);
                    break;
                case 3:
                    ut.test(r.nodeValue) && Rr(r, t, n)
            }
    }
    function Dr(e, t, n) {
        var r = e.getAttribute("ms-skip");
        if (!e.getAttributeNode)
            return u("warning " + e.tagName + " no getAttributeNode method");
        var i = e.getAttributeNode("ms-important")
            , s = e.getAttributeNode("ms-controller");
        if (typeof r == "string")
            return;
        if (n = i || s) {
            var o = avalon.vmodels[n.value];
            if (!o)
                return;
            t = n === i ? [o] : [o].concat(t);
            var a = n.name;
            e.removeAttribute(a),
                avalon(e).removeClass(a),
                hr(e, o)
        }
        Sr(e, t)
    }
    function Ir(e) {
        if (e.indexOf("|") > 0) {
            var t = e.replace(Fr, function(e) {
                return Array(e.length + 1).join("1")
            })
                , n = t.replace(Hr, "ᄢ㍄").indexOf("|");
            if (n > -1)
                return {
                    filters: e.slice(n),
                    value: e.slice(0, n),
                    expr: !0
                }
        }
        return {
            value: e,
            filters: "",
            expr: !0
        }
    }
    function qr(e) {
        var t = [], n, r = 0, i;
        do {
            i = e.indexOf(st, r);
            if (i === -1)
                break;
            n = e.slice(r, i),
            n && t.push({
                value: n,
                filters: "",
                expr: !1
            }),
                r = i + st.length,
                i = e.indexOf(ot, r);
            if (i === -1)
                break;
            n = e.slice(r, i),
            n && t.push(Ir(n, r)),
                r = i + ot.length
        } while (1);return n = e.slice(r),
        n && t.push({
            value: n,
            expr: !1,
            filters: ""
        }),
            t
    }
    function Rr(e, t, n) {
        var i = [];
        tokens = qr(e.data);
        if (tokens.length) {
            for (var s = 0; token = tokens[s++]; ) {
                var o = r.createTextNode(token.value);
                token.expr && (token.value = token.value.replace(mr, function() {
                    return token.oneTime = !0,
                        ""
                }),
                    token.type = "text",
                    token.element = o,
                    token.filters = token.filters.replace(Pr, function(e, t, n) {
                        return token.type = "html",
                            ""
                    }),
                    token.pos = n * 1e3 + s,
                    i.push(token)),
                    N.appendChild(o)
            }
            e.parentNode.replaceChild(N, e),
            i.length && dr(i, t)
        }
    }
    function Qr(e, t, n) {
        var i = e.templateCache && e.templateCache[t];
        if (i) {
            var s = r.createDocumentFragment(), o;
            while (o = i.firstChild)
                s.appendChild(o);
            return s
        }
        return avalon.parseHTML(n)
    }
    function Yr(e) {
        return e == null ? "" : e
    }
    function Zr(e, t, n, r) {
        return t.param.replace(/\w+/g, function(r) {
            var i = avalon.duplexHooks[r];
            i && typeof i[n] == "function" && (e = i[n](e, t))
        }),
            e
    }
    function ni() {
        for (var e = ti.length - 1; e >= 0; e--) {
            var t = ti[e];
            t() === !1 && ti.splice(e, 1)
        }
        ti.length || clearInterval(ei)
    }
    function oi(e, t, n, i) {
        var s = e.template.cloneNode(!0)
            , o = avalon.slice(s.childNodes);
        s.insertBefore(r.createComment(e.signature), s.firstChild),
            t.appendChild(s);
        var u = [n].concat(e.vmodels)
            , a = {
            nodes: o,
            vmodels: u
        };
        i.push(a)
    }
    function ui(e) {
        var t = []
            , n = e.element.parentNode.childNodes;
        for (var r = 0, i; i = n[r++]; )
            if (i.nodeValue === e.signature)
                t.push(i);
            else if (i.nodeValue === e.signature + ":end")
                break;
        return t
    }
    function ai(e, t, n) {
        for (; ; ) {
            var r = t.previousSibling;
            if (!r)
                break;
            r.parentNode.removeChild(r),
            n && n.call(r);
            if (r === e)
                break
        }
    }
    function li() {
        var e = St({
            $key: "",
            $outer: {},
            $host: {},
            $val: {
                get: function() {
                    return this.$host[this.$key]
                },
                set: function(e) {
                    this.$host[this.$key] = e
                }
            }
        }, {
            $val: 1
        });
        return e.$id = O("$proxy$with"),
            e
    }
    function ci(e, t, n) {
        e = e || fi.pop(),
            e ? e.$reinitialize() : e = li();
        var r = n.$repeat;
        return e.$key = t,
            e.$host = r,
            e.$outer = n.$outer,
            r.$events ? e.$events.$val = r.$events[t] : e.$events = {},
            e
    }
    function hi(e) {
        pi(e)
    }
    function pi(e) {
        e.forEach(function(e) {
            gi(e, di)
        }),
            e.length = 0
    }
    function vi(e) {
        var t = {
            $host: [],
            $outer: {},
            $index: 0,
            $first: !1,
            $last: !1,
            $remove: avalon.noop
        };
        t[e] = {
            get: function() {
                var t = this.$events
                    , n = t.$index;
                t.$index = t[e];
                try {
                    return this.$host[this.$index]
                } finally {
                    t.$index = n
                }
            },
            set: function(e) {
                try {
                    var t = this.$events
                        , n = t.$index;
                    t.$index = [],
                        this.$host.set(this.$index, e)
                } finally {
                    t.$index = n
                }
            }
        };
        var n = {
            $last: 1,
            $first: 1,
            $index: 1
        }
            , r = St(t, n);
        return r.$id = O("$proxy$each"),
            r
    }
    function mi(e, t) {
        var n = t.param || "el", r;
        for (var i = 0, s = di.length; i < s; i++) {
            var o = di[i];
            o && o.hasOwnProperty(n) && (r = o,
                di.splice(i, 1))
        }
        r || (r = vi(n));
        var u = t.$repeat
            , a = u.length - 1;
        return r.$index = e,
            r.$first = e === 0,
            r.$last = e === a,
            r.$host = u,
            r.$outer = t.$outer,
            r.$remove = function() {
                return u.removeAt(r.$index)
            }
            ,
            r
    }
    function gi(e, t) {
        for (var n in e.$events) {
            var r = e.$events[n];
            Array.isArray(r) && (r.forEach(function(e) {
                typeof e == "object" && tn(e)
            }),
                r.length = 0)
        }
        e.$host = e.$outer = {},
        t.unshift(e) > it.maxRepeatSize && t.pop()
    }
    function yi(e, t) {
        var n = "_" + e;
        if (!yi[n]) {
            var i = r.createElement(e);
            T.appendChild(i),
                x ? t = getComputedStyle(i, null).display : t = i.currentStyle.display,
                T.removeChild(i),
                yi[n] = t
        }
        return yi[n]
    }
    function Ci(e, t, n, r) {
        e = (e + "").replace(/[^0-9+\-Ee.]/g, "");
        var i = isFinite(+e) ? +e : 0
            , s = isFinite(+t) ? Math.abs(t) : 3
            , o = r || ","
            , u = n || "."
            , a = ""
            , f = function(e, t) {
            var n = Math.pow(10, t);
            return "" + (Math.round(e * n) / n).toFixed(t)
        };
        return a = (s ? f(i, s) : "" + Math.round(i)).split("."),
        a[0].length > 3 && (a[0] = a[0].replace(/\B(?=(?:\d{3})+(?!\d))/g, o)),
        (a[1] || "").length < s && (a[1] = a[1] || "",
            a[1] += (new Array(s - a[1].length + 1)).join("0")),
            a.join(u)
    }
    var n = new Date - 0
        , r = e.document
        , i = r.getElementsByTagName("head")[0]
        , s = i.insertBefore(document.createElement("avalon"), i.firstChild);
    s.innerHTML = "X<style id='avalonStyle'>.avalonHide{ display: none!important }</style>",
        s.setAttribute("ms-skip", "1"),
        s.className = "avalonHide";
    var o = /\[native code\]/, a = "$" + n, f = e.require, l = e.define, c, h = !1, p = /[^, ]+/g, d = /^(?:object|array)$/, v = /^\[object SVG\w*Element\]$/, m = /^\[object (?:Window|DOMWindow|global)\]$/, g = Object.prototype, y = g.hasOwnProperty, b = g.toString, w = Array.prototype, E = w.slice, S = {}, x = e.dispatchEvent, T = r.documentElement, N = r.createDocumentFragment(), C = r.createElement("div"), k = {};
    "Boolean Number String Function Array Date RegExp Object Error".replace(p, function(e) {
        k["[object " + e + "]"] = e.toLowerCase()
    });
    var O = function(e) {
        return e = e || "avalon",
            String(Math.random() + Math.random()).replace(/\d\.\d{4}/, e)
    }
        , _ = M();
    avalon = function(e) {
        return new avalon.init(e)
    }
        ,
        avalon.profile = function() {
            e.console && avalon.config.profile && Function.apply.call(console.log, console, arguments)
        }
        ,
        avalon.nextTick = new function() {
            function o() {
                var e = s.length;
                for (var t = 0; t < e; t++)
                    s[t]();
                s = s.slice(e)
            }
            var t = e.setImmediate
                , n = e.MutationObserver;
            if (t)
                return t.bind(e);
            var s = [];
            if (n) {
                var u = document.createTextNode("avalon");
                return (new n(o)).observe(u, {
                    characterData: !0
                }),
                    function(e) {
                        s.push(e),
                            u.data = Math.random()
                    }
            }
            return e.VBArray ? function(e) {
                s.push(e);
                var t = r.createElement("script");
                t.onreadystatechange = function() {
                    o(),
                        t.onreadystatechange = null,
                        i.removeChild(t),
                        t = null
                }
                    ,
                    i.appendChild(t)
            }
                : function(e) {
                setTimeout(e, 4)
            }
        }
        ,
        avalon.init = function(e) {
            this[0] = this.element = e
        }
        ,
        avalon.fn = avalon.prototype = avalon.init.prototype,
        avalon.type = function(e) {
            return e == null ? String(e) : typeof e == "object" || typeof e == "function" ? k[b.call(e)] || "object" : typeof e
        }
    ;
    var D = typeof alert == "object" ? function(e) {
            try {
                return /^\s*\bfunction\b/.test(e + "")
            } catch (t) {
                return !1
            }
        }
            : function(e) {
            return b.call(e) === "[object Function]"
        }
        ;
    avalon.isFunction = D,
        avalon.isWindow = function(e) {
            return e ? e == e.document && e.document != e : !1
        }
        ,
    P(e) && (avalon.isWindow = P);
    var H;
    for (H in avalon({}))
        break;
    var B = H !== "0";
    avalon.isPlainObject = function(e, t) {
        if (!e || avalon.type(e) !== "object" || e.nodeType || avalon.isWindow(e))
            return !1;
        try {
            if (e.constructor && !y.call(e, "constructor") && !y.call(e.constructor.prototype, "isPrototypeOf"))
                return !1
        } catch (n) {
            return !1
        }
        if (B)
            for (t in e)
                return y.call(e, t);
        for (t in e)
            ;
        return t === void 0 || y.call(e, t)
    }
        ,
    o.test(Object.getPrototypeOf) && (avalon.isPlainObject = function(e) {
            return b.call(e) === "[object Object]" && Object.getPrototypeOf(e) === g
        }
    ),
        avalon.mix = avalon.fn.mix = function() {
            var e, t, n, r, i, s, o = arguments[0] || {}, u = 1, a = arguments.length, f = !1;
            typeof o == "boolean" && (f = o,
                o = arguments[1] || {},
                u++),
            typeof o != "object" && !D(o) && (o = {}),
            u === a && (o = this,
                u--);
            for (; u < a; u++)
                if ((e = arguments[u]) != null)
                    for (t in e) {
                        n = o[t];
                        try {
                            r = e[t]
                        } catch (l) {
                            continue
                        }
                        if (o === r)
                            continue;
                        f && r && (avalon.isPlainObject(r) || (i = Array.isArray(r))) ? (i ? (i = !1,
                            s = n && Array.isArray(n) ? n : []) : s = n && avalon.isPlainObject(n) ? n : {},
                            o[t] = avalon.mix(f, s, r)) : r !== void 0 && (o[t] = r)
                    }
            return o
        }
        ,
        avalon.mix({
            rword: p,
            subscribers: a,
            version: 1.46,
            ui: {},
            log: u,
            slice: x ? function(e, t, n) {
                return E.call(e, t, n)
            }
                : function(e, t, n) {
                var r = []
                    , i = e.length;
                n === void 0 && (n = i);
                if (typeof n == "number" && isFinite(n)) {
                    t = j(t, i),
                        n = j(n, i);
                    for (var s = t; s < n; ++s)
                        r[s - t] = e[s]
                }
                return r
            }
            ,
            noop: L,
            error: function(e, t) {
                throw (t || Error)(e)
            },
            oneObject: A,
            range: function(e, t, n) {
                n || (n = 1),
                t == null && (t = e || 0,
                    e = 0);
                var r = -1
                    , i = Math.max(0, Math.ceil((t - e) / n))
                    , s = new Array(i);
                while (++r < i)
                    s[r] = e,
                        e += n;
                return s
            },
            eventHooks: [],
            bind: function(e, t, n, r) {
                var i = avalon.eventHooks
                    , s = i[t];
                typeof s == "object" && (t = s.type,
                s.deel && (n = s.deel(e, t, n, r)));
                var o = x ? n : function(t) {
                        n.call(e, et(t))
                    }
                    ;
                return x ? e.addEventListener(t, o, !!r) : e.attachEvent("on" + t, o),
                    o
            },
            unbind: function(e, t, n, r) {
                var i = avalon.eventHooks
                    , s = i[t]
                    , o = n || L;
                typeof s == "object" && (t = s.type,
                s.deel && (n = s.deel(e, t, n, !1))),
                    x ? e.removeEventListener(t, o, !!r) : e.detachEvent("on" + t, o)
            },
            css: function(e, t, n) {
                e instanceof avalon && (e = e[0]);
                var r = /[_-]/.test(t) ? vn(t) : t, i;
                t = avalon.cssName(r) || r;
                if (n === void 0 || typeof n == "boolean") {
                    i = Nn[r + ":get"] || Nn["@:get"],
                    t === "background" && (t = "backgroundColor");
                    var s = i(e, t);
                    return n === !0 ? parseFloat(s) || 0 : s
                }
                if (n === "")
                    e.style[t] = "";
                else {
                    if (n == null || n !== n)
                        return;
                    isFinite(n) && !avalon.cssNumber[r] && (n += "px"),
                        i = Nn[r + ":set"] || Nn["@:set"],
                        i(e, t, n)
                }
            },
            each: function(e, t) {
                if (e) {
                    var n = 0;
                    if (q(e)) {
                        for (var r = e.length; n < r; n++)
                            if (t(n, e[n]) === !1)
                                break
                    } else
                        for (n in e)
                            if (e.hasOwnProperty(n) && t(n, e[n]) === !1)
                                break
                }
            },
            getWidgetData: function(e, t) {
                var n = avalon(e).data()
                    , r = {};
                for (var i in n)
                    i.indexOf(t) === 0 && (r[i.replace(t, "").replace(/\w/, function(e) {
                        return e.toLowerCase()
                    })] = n[i]);
                return r
            },
            Array: {
                ensure: function(e, t) {
                    if (e.indexOf(t) === -1)
                        return e.push(t)
                },
                removeAt: function(e, t) {
                    return !!e.splice(t, 1).length
                },
                remove: function(e, t) {
                    var n = e.indexOf(t);
                    return ~n ? avalon.Array.removeAt(e, n) : !1
                }
            }
        });
    var F = avalon.bindingHandlers = {}
        , I = avalon.bindingExecutors = {}
        , R = new function() {
            function e(e) {
                this.size = 0,
                    this.limit = e,
                    this.head = this.tail = void 0,
                    this._keymap = {}
            }
            var t = e.prototype;
            return t.put = function(e, t) {
                var n = {
                    key: e,
                    value: t
                };
                return this._keymap[e] = n,
                    this.tail ? (this.tail.newer = n,
                        n.older = this.tail) : this.head = n,
                    this.tail = n,
                    this.size === this.limit ? this.shift() : this.size++,
                    t
            }
                ,
                t.shift = function() {
                    var e = this.head;
                    e && (this.head = this.head.newer,
                        this.head.older = e.newer = e.older = this._keymap[e.key] = void 0)
                }
                ,
                t.get = function(e) {
                    var t = this._keymap[e];
                    if (t === void 0)
                        return;
                    return t === this.tail ? t.value : (t.newer && (t === this.head && (this.head = t.newer),
                        t.newer.older = t.older),
                    t.older && (t.older.newer = t.newer),
                        t.newer = void 0,
                        t.older = this.tail,
                    this.tail && (this.tail.newer = t),
                        this.tail = t,
                        t.value)
                }
                ,
                e
        }
        ;
    if (!"司徒正美".trim) {
        var U = /^[\s\uFEFF\xA0]+|[\s\uFEFF\xA0]+$/g;
        String.prototype.trim = function() {
            return this.replace(U, "")
        }
    }
    var z = !{
        toString: null
    }.propertyIsEnumerable("toString")
        , W = function() {}
        .propertyIsEnumerable("prototype")
        , X = ["toString", "toLocaleString", "valueOf", "hasOwnProperty", "isPrototypeOf", "propertyIsEnumerable", "constructor"]
        , V = X.length;
    Object.keys || (Object.keys = function(e) {
            var t = []
                , n = W && typeof e == "function";
            if (typeof e == "string" || e && e.callee)
                for (var r = 0; r < e.length; ++r)
                    t.push(String(r));
            else
                for (var i in e)
                    (!n || i !== "prototype") && y.call(e, i) && t.push(String(i));
            if (z) {
                var s = e.constructor
                    , o = s && s.prototype === e;
                for (var u = 0; u < V; u++) {
                    var a = X[u];
                    (!o || a !== "constructor") && y.call(e, a) && t.push(a)
                }
            }
            return t
        }
    ),
    Array.isArray || (Array.isArray = function(e) {
            return b.call(e) === "[object Array]"
        }
    ),
    L.bind || (Function.prototype.bind = function(e) {
            if (arguments.length < 2 && e === void 0)
                return this;
            var t = this
                , n = arguments;
            return function() {
                var r = [], i;
                for (i = 1; i < n.length; i++)
                    r.push(n[i]);
                for (i = 0; i < arguments.length; i++)
                    r.push(arguments[i]);
                return t.apply(e, r)
            }
        }
    ),
    o.test([].map) || avalon.mix(w, {
        indexOf: function(e, t) {
            var n = this.length
                , r = ~~t;
            r < 0 && (r += n);
            for (; r < n; r++)
                if (this[r] === e)
                    return r;
            return -1
        },
        lastIndexOf: function(e, t) {
            var n = this.length
                , r = t == null ? n - 1 : t;
            r < 0 && (r = Math.max(0, n + r));
            for (; r >= 0; r--)
                if (this[r] === e)
                    return r;
            return -1
        },
        forEach: $("", "_", ""),
        filter: $("r=[],j=0,", "if(_)r[j++]=this[i]", "return r"),
        map: $("r=[],", "r[i]=_", "return r"),
        some: $("", "if(_)return true", "return false"),
        every: $("", "if(!_)return false", "return true")
    }),
        avalon.contains = J,
    r.contains || (r.contains = function(e) {
            return J(r, e)
        }
    );
    if (e.SVGElement) {
        r.createTextNode("x").contains || (Node.prototype.contains = function(e) {
                return !!(this.compareDocumentPosition(e) & 16)
            }
        );
        var Q = "http://www.w3.org/2000/svg"
            , G = r.createElementNS(Q, "svg");
        G.innerHTML = '<circle cx="50" cy="50" r="40" fill="red" />';
        if (!v.test(G.firstChild)) {
            function Y(e, t) {
                if (e && e.childNodes) {
                    var n = e.childNodes;
                    for (var i = 0, s; s = n[i++]; )
                        if (s.tagName) {
                            var o = r.createElementNS(Q, s.tagName.toLowerCase());
                            w.forEach.call(s.attributes, function(e) {
                                o.setAttribute(e.name, e.value)
                            }),
                                Y(s, o),
                                t.appendChild(o)
                        }
                }
            }
            Object.defineProperties(SVGElement.prototype, {
                outerHTML: {
                    enumerable: !0,
                    configurable: !0,
                    get: K,
                    set: function(e) {
                        var t = this.tagName.toLowerCase()
                            , n = this.parentNode
                            , i = avalon.parseHTML(e);
                        if (t === "svg")
                            n.insertBefore(i, this);
                        else {
                            var s = r.createDocumentFragment();
                            Y(i, s),
                                n.insertBefore(s, this)
                        }
                        n.removeChild(this)
                    }
                },
                innerHTML: {
                    enumerable: !0,
                    configurable: !0,
                    get: function() {
                        var e = this.outerHTML
                            , t = new RegExp("<" + this.nodeName + '\\b(?:(["\'])[^"]*?(\\1)|[^>])*>',"i")
                            , n = new RegExp("</" + this.nodeName + ">$","i");
                        return e.replace(t, "").replace(n, "")
                    },
                    set: function(e) {
                        if (avalon.clearHTML) {
                            avalon.clearHTML(this);
                            var t = avalon.parseHTML(e);
                            Y(t, this)
                        }
                    }
                }
            })
        }
    }
    !T.outerHTML && e.HTMLElement && HTMLElement.prototype.__defineGetter__("outerHTML", K);
    var Z = /^(?:mouse|contextmenu|drag)|click/
        , tt = avalon.eventHooks;
    "onmouseenter"in T || avalon.each({
        mouseenter: "mouseover",
        mouseleave: "mouseout"
    }, function(e, t) {
        tt[e] = {
            type: t,
            deel: function(t, n, r) {
                return function(n) {
                    var i = n.relatedTarget;
                    if (!i || i !== t && !(t.compareDocumentPosition(i) & 16))
                        return delete n.type,
                            n.type = e,
                            r.call(t, n)
                }
            }
        }
    }),
        avalon.each({
            AnimationEvent: "animationend",
            WebKitAnimationEvent: "webkitAnimationEnd"
        }, function(t, n) {
            e[t] && !tt.animationend && (tt.animationend = {
                type: n
            })
        }),
    "oninput"in r.createElement("input") || (tt.input = {
        type: "propertychange",
        deel: function(e, t, n) {
            return function(t) {
                if (t.propertyName === "value")
                    return t.type = "input",
                        n.call(e, t)
            }
        }
    });
    if (r.onmousewheel === void 0) {
        var nt = r.onwheel !== void 0 ? "wheel" : "DOMMouseScroll"
            , rt = nt === "wheel" ? "deltaY" : "detail";
        tt.mousewheel = {
            type: nt,
            deel: function(e, t, n) {
                return function(t) {
                    t.wheelDeltaY = t.wheelDelta = t[rt] > 0 ? -120 : 120,
                        t.wheelDeltaX = 0,
                    Object.defineProperty && Object.defineProperty(t, "type", {
                        value: "mousewheel"
                    }),
                        n.call(e, t)
                }
            }
        }
    }
    var st, ot, ut, at, ft, lt = /[-.*+?^${}()|[\]\/\\]/g, ht = {
        loader: function(t) {
            var n = c && t;
            e.require = n ? c : f,
                e.define = n ? c.define : l
        },
        interpolate: function(e) {
            st = e[0],
                ot = e[1];
            if (st === ot)
                throw new SyntaxError("openTag===closeTag");
            var t = st + "test" + ot;
            C.innerHTML = t;
            if (C.innerHTML !== t && C.innerHTML.indexOf("&lt;") > -1)
                throw new SyntaxError("此定界符不合法");
            it.openTag = st,
                it.closeTag = ot,
                C.innerHTML = "";
            var n = ct(st)
                , r = ct(ot);
            ut = new RegExp(n + "(.*?)" + r),
                at = new RegExp(n + "(.*?)" + r,"g"),
                ft = new RegExp(n + ".*?" + r + "|\\sms-")
        }
    };
    it.debug = !0,
        it.plugins = ht,
        it.plugins.interpolate(["{{", "}}"]),
        it.paths = {},
        it.shim = {},
        it.maxRepeatSize = 100,
        avalon.config = it;
    var pt = /(\w+)\[(avalonctrl)="(\S+)"\]/
        , dt = r.querySelectorAll ? function(e) {
        return r.querySelectorAll(e)
    }
        : function(e) {
        var t = e.match(pt)
            , n = r.getElementsByTagName(t[1])
            , i = [];
        for (var s = 0, o; o = n[s++]; )
            o.getAttribute(t[2]) === t[3] && i.push(o);
        return i
    }
        , vt = {
        $watch: function(e, t) {
            if (typeof t == "function") {
                var n = this.$events[e];
                n ? n.push(t) : this.$events[e] = [t]
            } else
                this.$events = this.$watch.backup;
            return this
        },
        $unwatch: function(e, t) {
            var n = arguments.length;
            if (n === 0)
                this.$watch.backup = this.$events,
                    this.$events = {};
            else if (n === 1)
                this.$events[e] = [];
            else {
                var r = this.$events[e] || []
                    , i = r.length;
                while (~--i < 0)
                    if (r[i] === t)
                        return r.splice(i, 1)
            }
            return this
        },
        $fire: function(e) {
            var t, n, i, s;
            /^(\w+)!(\S+)$/.test(e) && (t = RegExp.$1,
                e = RegExp.$2);
            var o = this.$events;
            if (!o)
                return;
            var u = E.call(arguments, 1)
                , a = [e].concat(u);
            if (t === "all")
                for (n in avalon.vmodels)
                    i = avalon.vmodels[n],
                    i !== this && i.$fire.apply(i, a);
            else if (t === "up" || t === "down") {
                var f = o.expr ? dt(o.expr) : [];
                if (f.length === 0)
                    return;
                for (n in avalon.vmodels) {
                    i = avalon.vmodels[n];
                    if (i !== this && i.$events.expr) {
                        var l = dt(i.$events.expr);
                        if (l.length === 0)
                            continue;
                        w.forEach.call(l, function(e) {
                            w.forEach.call(f, function(n) {
                                var r = t === "down" ? n.contains(e) : e.contains(n);
                                r && (e._avalon = i)
                            })
                        })
                    }
                }
                var c = r.getElementsByTagName("*")
                    , h = [];
                w.forEach.call(c, function(e) {
                    e._avalon && (h.push(e._avalon),
                        e._avalon = "",
                        e.removeAttribute("_avalon"))
                }),
                t === "up" && h.reverse();
                for (n = 0; s = h[n++]; )
                    if (s.$fire.apply(s, a) === !1)
                        break
            } else {
                var p = o[e] || []
                    , d = o.$all || [];
                for (n = 0; s = p[n++]; )
                    D(s) && s.apply(this, u);
                for (n = 0; s = d[n++]; )
                    D(s) && s.apply(this, arguments)
            }
        }
    }
        , mt = avalon.vmodels = {};
    avalon.define = function(e, t) {
        var n = e.$id || e;
        n || u("warning: vm必须指定$id"),
        mt[n] && u("warning: " + n + " 已经存在于avalon.vmodels中");
        if (typeof e == "object")
            var r = St(e);
        else {
            var i = {
                $watch: L
            };
            t(i),
                r = St(i),
                h = !0,
                t(r),
                h = !1
        }
        return r.$id = n,
            mt[n] = r
    }
    ;
    var gt = String("$id,$watch,$unwatch,$fire,$events,$model,$skipArray,$reinitialize").match(p)
        , yt = Object.defineProperty
        , bt = !0;
    try {
        yt({}, "_", {
            value: "x"
        });
        var wt = Object.defineProperties
    } catch (Et) {
        bt = !1
    }
    var Ot = Object.is || function(e, t) {
                return e === 0 && t === 0 ? 1 / e === 1 / t : e !== e ? t !== t : e === t
            }
        , Dt = x ? function(e) {
            var t = {};
            for (var n in e)
                t[n] = {
                    get: e[n],
                    set: e[n],
                    enumerable: !0,
                    configurable: !0
                };
            return t
        }
            : function(e) {
            return e
        }
        ;
    if (!bt) {
        "__defineGetter__"in avalon && (yt = function(e, t, n) {
                return "value"in n && (e[t] = n.value),
                "get"in n && e.__defineGetter__(t, n.get),
                "set"in n && e.__defineSetter__(t, n.set),
                    e
            }
                ,
                wt = function(e, t) {
                    for (var n in t)
                        t.hasOwnProperty(n) && yt(e, n, t[n]);
                    return e
                }
        );
        if (_) {
            var Pt = {};
            e.execScript(["Function parseVB(code)", "	ExecuteGlobal(code)", "End Function"].join("\n"), "VBScript");
            function Ht(e, t, n, r) {
                var i = t[n];
                if (arguments.length !== 4)
                    return i.call(e);
                i.call(e, r)
            }
            wt = function(t, r, i) {
                var s = [];
                s.push("\r\n	Private [__data__], [__proxy__]", "	Public Default Function [__const__](d" + n + ", p" + n + ")", "		Set [__data__] = d" + n + ": set [__proxy__] = p" + n, "		Set [__const__] = Me", "	End Function");
                for (t in i)
                    r.hasOwnProperty(t) || s.push("	Public [" + t + "]");
                gt.forEach(function(e) {
                    r.hasOwnProperty(e) || s.push("	Public [" + e + "]")
                }),
                    s.push("	Public [hasOwnProperty]");
                for (t in r)
                    s.push("	Public Property Let [" + t + "](val" + n + ")", '		Call [__proxy__](Me,[__data__], "' + t + '", val' + n + ")", "	End Property", "	Public Property Set [" + t + "](val" + n + ")", '		Call [__proxy__](Me,[__data__], "' + t + '", val' + n + ")", "	End Property", "	Public Property Get [" + t + "]", "	On Error Resume Next", "		Set[" + t + '] = [__proxy__](Me,[__data__],"' + t + '")', "	If Err.Number <> 0 Then", "		[" + t + '] = [__proxy__](Me,[__data__],"' + t + '")', "	End If", "	On Error Goto 0", "	End Property");
                s.push("End Class");
                var o = s.join("\r\n")
                    , u = Pt[o];
                u || (u = O("VBClass"),
                    e.parseVB("Class " + u + o),
                    e.parseVB(["Function " + u + "Factory(a, b)", "	Dim o", "	Set o = (New " + u + ")(a, b)", "	Set " + u + "Factory = o", "End Function"].join("\r\n")),
                    Pt[o] = u);
                var a = e[u + "Factory"](r, Ht);
                return a
            }
        }
    }
    var Ft = w.splice
        , It = {
        _splice: Ft,
        _fire: function(e, t, n) {
            Xt(this.$events[a], e, t, n)
        },
        size: function() {
            return this._.length
        },
        pushArray: function(e) {
            var t = e.length
                , n = this.length;
            return t && (w.push.apply(this.$model, e),
                jt.call(this, "add", n, t, Math.max(0, n - 1))),
            t + n
        },
        push: function() {
            var e = [], t, n = arguments.length;
            for (t = 0; t < n; t++)
                e[t] = arguments[t];
            return this.pushArray(e)
        },
        unshift: function() {
            var e = arguments.length
                , t = this.length;
            return e && (w.unshift.apply(this.$model, arguments),
                jt.call(this, "add", 0, e, 0)),
            e + t
        },
        shift: function() {
            if (this.length) {
                var e = this.$model.shift();
                return jt.call(this, "del", 0, 1, 0),
                    e
            }
        },
        pop: function() {
            var e = this.length;
            if (e) {
                var t = this.$model.pop();
                return jt.call(this, "del", e - 1, 1, Math.max(0, e - 2)),
                    t
            }
        },
        splice: function(e) {
            var t = arguments.length, n = [], r, i = Ft.apply(this.$model, arguments);
            return i.length && (n.push("del", e, i.length, 0),
                r = !0),
            t > 2 && (r ? n.splice(3, 1, 0, "add", e, t - 2) : n.push("add", e, t - 2, 0),
                r = !0),
                r ? jt.apply(this, n) : []
        },
        contains: function(e) {
            return this.indexOf(e) !== -1
        },
        remove: function(e) {
            return this.removeAt(this.indexOf(e))
        },
        removeAt: function(e) {
            return e >= 0 ? (this.$model.splice(e, 1),
                jt.call(this, "del", e, 1, 0)) : []
        },
        clear: function() {
            return this.$model.length = this.length = this._.length = 0,
                this._fire("clear", 0),
                this
        },
        removeAll: function(e) {
            if (Array.isArray(e))
                for (var t = this.length - 1; t >= 0; t--)
                    e.indexOf(this[t]) !== -1 && this.removeAt(t);
            else if (typeof e == "function")
                for (t = this.length - 1; t >= 0; t--) {
                    var n = this[t];
                    e(n, t) && this.removeAt(t)
                }
            else
                this.clear()
        },
        ensure: function(e) {
            return this.contains(e) || this.push(e),
                this
        },
        set: function(e, t) {
            if (e < this.length && e > -1) {
                var n = avalon.type(t);
                t && t.$model && (t = t.$model);
                var r = this[e];
                if (n === "object")
                    for (var i in t)
                        r.hasOwnProperty(i) && (r[i] = t[i]);
                else
                    n === "array" ? r.clear().push.apply(r, t) : r !== t && (this[e] = t,
                        this.$model[e] = t,
                        this._fire("set", e, t))
            }
            return this
        }
    };
    "sort,reverse".replace(p, function(e) {
        It[e] = function() {
            var t = this.$model, n = t.concat(), r = Math.random(), i = [], s;
            w[e].apply(t, arguments);
            for (var o = 0, u = n.length; o < u; o++) {
                var a = t[o]
                    , f = n[o];
                if (Ot(a, f))
                    i.push(o);
                else {
                    var l = n.indexOf(a);
                    i.push(l),
                        n[l] = r,
                        s = !0
                }
            }
            return s && (Rt(this, i),
                this._fire("move", i),
                this._fire("index", 0)),
                this
        }
    });
    var Ut = function() {
        var e = [], t;
        return {
            begin: function(n) {
                e.push(t),
                    t = n
            },
            end: function() {
                t = e.pop()
            },
            collectDependency: function(e, n) {
                t && t.callback(e, n)
            }
        }
    }()
        , zt = /^(duplex|on)$/;
    avalon.injectBinding = function(e) {
        var t = e.evaluator;
        if (t) {
            Ut.begin({
                callback: function(t, n) {
                    Wt(t.$events[n._name], e)
                }
            });
            try {
                var n = zt.test(e.type) ? e : t.apply(0, e.args);
                n === void 0 && delete e.evaluator,
                    e.handler(n, e.element, e)
            } catch (i) {
                u("warning:exception throwed in [avalon.injectBinding] ", i),
                    delete e.evaluator;
                var s = e.element;
                if (s.nodeType === 3) {
                    var o = s.parentNode;
                    it.commentInterpolate ? o.replaceChild(r.createComment(e.value), s) : s.data = st + (e.oneTime ? "::" : "") + e.value + ot
                }
            } finally {
                Ut.end()
            }
        }
    }
    ;
    var Vt = 0
        , $t = avalon.$$subscribers = []
        , Jt = new Date
        , Kt = {}
        , Qt = {}
        , rn = {
        area: [1, "<map>", "</map>"],
        param: [1, "<object>", "</object>"],
        col: [2, "<table><colgroup>", "</colgroup></table>"],
        legend: [1, "<fieldset>", "</fieldset>"],
        option: [1, "<select multiple='multiple'>", "</select>"],
        thead: [1, "<table>", "</table>"],
        tr: [2, "<table>", "</table>"],
        td: [3, "<table><tr>", "</tr></table>"],
        g: [1, '<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" version="1.1">', "</svg>"],
        _default: x ? [0, "", ""] : [1, "X<div>", "</div>"]
    };
    rn.th = rn.td,
        rn.optgroup = rn.option,
        rn.tbody = rn.tfoot = rn.colgroup = rn.caption = rn.thead,
        String("circle,defs,ellipse,image,line,path,polygon,polyline,rect,symbol,text,use").replace(p, function(e) {
            rn[e] = rn.g
        });
    var sn = /<([\w:]+)/
        , on = /<(?!area|br|col|embed|hr|img|input|link|meta|param)(([\w:]+)[^>]*)\/>/ig
        , un = x ? /[^\d\D]/ : /(<(?:script|link|style|meta|noscript))/ig
        , an = A(["", "text/javascript", "text/ecmascript", "application/ecmascript", "application/javascript"])
        , fn = /<(?:tb|td|tf|th|tr|col|opt|leg|cap|area)/
        , ln = r.createElement("script")
        , cn = /<|&#?\w+;/;
    avalon.parseHTML = function(e) {
        var t = N.cloneNode(!1);
        if (typeof e != "string")
            return t;
        if (!cn.test(e))
            return t.appendChild(r.createTextNode(e)),
                t;
        e = e.replace(on, "<$1></$2>").trim();
        var n = (sn.exec(e) || ["", ""])[1].toLowerCase(), i = rn[n] || rn._default, s = C, o, u;
        x || (e = e.replace(un, "<br class=msNoScope>$1")),
            s.innerHTML = i[1] + e + i[2];
        var a = s.getElementsByTagName("script");
        if (a.length)
            for (var f = 0, l; l = a[f++]; )
                an[l.type] && (u = ln.cloneNode(!1),
                    w.forEach.call(l.attributes, function(e) {
                        e && e.specified && (u[e.name] = e.value,
                            u.setAttribute(e.name, e.value))
                    }),
                    u.text = l.text,
                    l.parentNode.replaceChild(u, l));
        if (!x) {
            var c = i[1] === "X<div>" ? s.lastChild.firstChild : s.lastChild;
            if (c && c.tagName === "TABLE" && n !== "tbody")
                for (a = c.childNodes,
                         f = 0; l = a[f++]; )
                    if (l.tagName === "TBODY" && !l.innerHTML) {
                        c.removeChild(l);
                        break
                    }
            a = s.getElementsByTagName("br");
            var h = a.length;
            while (l = a[--h])
                l.className === "msNoScope" && l.parentNode.removeChild(l);
            for (a = s.all,
                     f = 0; l = a[f++]; )
                hn(l) && pn(l)
        }
        for (f = i[0]; f--; s = s.lastChild)
            ;
        while (o = s.firstChild)
            t.appendChild(o);
        return t
    }
        ,
        avalon.innerHTML = function(e, t) {
            if (!x && !un.test(t) && !fn.test(t))
                try {
                    e.innerHTML = t;
                    return
                } catch (n) {}
            var r = this.parseHTML(t);
            this.clearHTML(e).appendChild(r)
        }
        ,
        avalon.clearHTML = function(e) {
            e.textContent = "";
            while (e.firstChild)
                e.removeChild(e.firstChild);
            return e
        }
    ;
    var mn = {
        _toString: function() {
            var e = this.node
                , t = e.className
                , n = typeof t == "string" ? t : t.baseVal;
            return n.split(/\s+/).join(" ")
        },
        _contains: function(e) {
            return (" " + this + " ").indexOf(" " + e + " ") > -1
        },
        _add: function(e) {
            this.contains(e) || this._set(this + " " + e)
        },
        _remove: function(e) {
            this._set((" " + this + " ").replace(" " + e + " ", " "))
        },
        __set: function(e) {
            e = e.trim();
            var t = this.node;
            v.test(t) ? t.setAttribute("class", e) : t.className = e
        }
    };
    "add,remove".replace(p, function(e) {
        avalon.fn[e + "Class"] = function(t) {
            var n = this[0];
            return t && typeof t == "string" && n && n.nodeType === 1 && t.replace(/\S+/g, function(t) {
                gn(n)[e](t)
            }),
                this
        }
    }),
        avalon.fn.mix({
            hasClass: function(e) {
                var t = this[0] || {};
                return t.nodeType === 1 && gn(t).contains(e)
            },
            toggleClass: function(e, t) {
                var n, r = 0, i = String(e).split(/\s+/), s = typeof t == "boolean";
                while (n = i[r++]) {
                    var o = s ? t : !this.hasClass(n);
                    this[o ? "addClass" : "removeClass"](n)
                }
                return this
            },
            attr: function(e, t) {
                return arguments.length === 2 ? (this[0].setAttribute(e, t),
                    this) : this[0].getAttribute(e)
            },
            data: function(e, t) {
                e = "data-" + dn(e || "");
                switch (arguments.length) {
                    case 2:
                        return this.attr(e, t),
                            this;
                    case 1:
                        var n = this.attr(e);
                        return yn(n);
                    case 0:
                        var r = {};
                        return w.forEach.call(this[0].attributes, function(t) {
                            t && (e = t.name,
                            e.indexOf("data-") || (e = vn(e.slice(5)),
                                r[e] = yn(t.value)))
                        }),
                            r
                }
            },
            removeData: function(e) {
                return e = "data-" + dn(e),
                    this[0].removeAttribute(e),
                    this
            },
            css: function(e, t) {
                if (avalon.isPlainObject(e))
                    for (var n in e)
                        avalon.css(this, n, e[n]);
                else
                    var r = avalon.css(this, e, t);
                return r !== void 0 ? r : this
            },
            position: function() {
                var e, t, n = this[0], r = {
                    top: 0,
                    left: 0
                };
                if (!n)
                    return;
                return this.css("position") === "fixed" ? t = n.getBoundingClientRect() : (e = this.offsetParent(),
                    t = this.offset(),
                e[0].tagName !== "HTML" && (r = e.offset()),
                    r.top += avalon.css(e[0], "borderTopWidth", !0),
                    r.left += avalon.css(e[0], "borderLeftWidth", !0),
                    r.top -= e.scrollTop(),
                    r.left -= e.scrollLeft()),
                {
                    top: t.top - r.top - avalon.css(n, "marginTop", !0),
                    left: t.left - r.left - avalon.css(n, "marginLeft", !0)
                }
            },
            offsetParent: function() {
                var e = this[0].offsetParent;
                while (e && avalon.css(e, "position") === "static")
                    e = e.offsetParent;
                return avalon(e || T)
            },
            bind: function(e, t, n) {
                if (this[0])
                    return avalon.bind(this[0], e, t, n)
            },
            unbind: function(e, t, n) {
                return this[0] && avalon.unbind(this[0], e, t, n),
                    this
            },
            val: function(e) {
                var t = this[0];
                if (t && t.nodeType === 1) {
                    var n = arguments.length === 0
                        , r = n ? ":get" : ":set"
                        , i = In[jn(t) + r];
                    if (i)
                        var s = i(t, e);
                    else {
                        if (n)
                            return (t.value || "").replace(/\r/g, "");
                        t.value = e
                    }
                }
                return n ? s : this
            }
        });
    var bn = /(?:\{[\s\S]*\}|\[[\s\S]*\])$/
        , wn = /^[\],:{}\s]*$/
        , En = /(?:^|:|,)(?:\s*\[)+/g
        , Sn = /\\(?:["\\\/bfnrt]|u[\da-fA-F]{4})/g
        , xn = /"[^"\\\r\n]*"|true|false|null|-?(?:\d+\.|)\d+(?:[eE][+-]?\d+|)/g;
    avalon.parseJSON = e.JSON ? JSON.parse : function(e) {
        if (typeof e == "string") {
            e = e.trim();
            if (e && wn.test(e.replace(Sn, "@").replace(xn, "]").replace(En, "")))
                return (new Function("return " + e))();
            avalon.error("Invalid JSON: " + e)
        }
        return e
    }
        ,
        avalon.each({
            scrollLeft: "pageXOffset",
            scrollTop: "pageYOffset"
        }, function(e, t) {
            avalon.fn[e] = function(n) {
                var r = this[0] || {}
                    , i = Tn(r)
                    , s = e === "scrollTop";
                if (!arguments.length)
                    return i ? t in i ? i[t] : T[e] : r[e];
                i ? i.scrollTo(s ? avalon(i).scrollLeft() : n, s ? n : avalon(i).scrollTop()) : r[e] = n
            }
        });
    var Nn = avalon.cssHooks = {}
        , Cn = ["", "-webkit-", "-o-", "-moz-", "-ms-"]
        , kn = {
        "float": x ? "cssFloat" : "styleFloat"
    };
    avalon.cssNumber = A("columnCount,order,flex,flexGrow,flexShrink,fillOpacity,fontWeight,lineHeight,opacity,orphans,widows,zIndex,zoom"),
        avalon.cssName = function(e, t, n) {
            if (kn[e])
                return kn[e];
            t = t || T.style;
            for (var r = 0, i = Cn.length; r < i; r++) {
                n = vn(Cn[r] + e);
                if (n in t)
                    return kn[e] = n
            }
            return null
        }
        ,
        Nn["@:set"] = function(e, t, n) {
            try {
                e.style[t] = n
            } catch (r) {}
        }
    ;
    if (e.getComputedStyle)
        Nn["@:get"] = function(e, t) {
            if (!e || !e.style)
                throw new Error("getComputedStyle要求传入一个节点 " + e);
            var n, r = getComputedStyle(e, null);
            return r && (n = t === "filter" ? r.getPropertyValue(t) : r[t],
            n === "" && (n = e.style[t])),
                n
        }
            ,
            Nn["opacity:get"] = function(e) {
                var t = Nn["@:get"](e, "opacity");
                return t === "" ? "1" : t
            }
        ;
    else {
        var Ln = /^-?(?:\d*\.)?\d+(?!px)[^\d\s]+$/i
            , An = /^(top|right|bottom|left)$/
            , On = /alpha\([^)]*\)/i
            , Mn = !!e.XDomainRequest
            , _n = "DXImageTransform.Microsoft.Alpha"
            , Dn = {
            thin: Mn ? "1px" : "2px",
            medium: Mn ? "3px" : "4px",
            thick: Mn ? "5px" : "6px"
        };
        Nn["@:get"] = function(e, t) {
            var n = e.currentStyle
                , r = n[t];
            if (Ln.test(r) && !An.test(r)) {
                var i = e.style
                    , s = i.left
                    , o = e.runtimeStyle.left;
                e.runtimeStyle.left = n.left,
                    i.left = t === "fontSize" ? "1em" : r || 0,
                    r = i.pixelLeft + "px",
                    i.left = s,
                    e.runtimeStyle.left = o
            }
            return r === "medium" && (t = t.replace("Width", "Style"),
            n[t] === "none" && (r = "0px")),
                r === "" ? "auto" : Dn[r] || r
        }
            ,
            Nn["opacity:set"] = function(e, t, n) {
                var r = e.style
                    , i = isFinite(n) && n <= 1 ? "alpha(opacity=" + n * 100 + ")" : ""
                    , s = r.filter || "";
                r.zoom = 1,
                    r.filter = (On.test(s) ? s.replace(On, i) : s + " " + i).trim(),
                r.filter || r.removeAttribute("filter")
            }
            ,
            Nn["opacity:get"] = function(e) {
                var t = e.filters.alpha || e.filters[_n]
                    , n = t && t.enabled ? t.opacity : 100;
                return n / 100 + ""
            }
    }
    "top,left".replace(p, function(e) {
        Nn[e + ":get"] = function(t) {
            var n = Nn["@:get"](t, e);
            return /px$/.test(n) ? n : avalon(t).position()[e] + "px"
        }
    });
    var Pn = {
        position: "absolute",
        visibility: "hidden",
        display: "block"
    }
        , Hn = /^(none|table(?!-c[ea]).+)/;
    "Width,Height".replace(p, function(e) {
        var t = e.toLowerCase()
            , n = "client" + e
            , r = "scroll" + e
            , i = "offset" + e;
        Nn[t + ":get"] = function(t, n, r) {
            var s = -4;
            typeof r == "number" && (s = r),
                n = e === "Width" ? ["Left", "Right"] : ["Top", "Bottom"];
            var o = t[i];
            return s === 2 ? o + avalon.css(t, "margin" + n[0], !0) + avalon.css(t, "margin" + n[1], !0) : (s < 0 && (o = o - avalon.css(t, "border" + n[0] + "Width", !0) - avalon.css(t, "border" + n[1] + "Width", !0)),
            s === -4 && (o = o - avalon.css(t, "padding" + n[0], !0) - avalon.css(t, "padding" + n[1], !0)),
                o)
        }
            ,
            Nn[t + "&get"] = function(e) {
                var n = [];
                Bn(e, n);
                var r = Nn[t + ":get"](e);
                for (var i = 0, s; s = n[i++]; ) {
                    e = s.node;
                    for (var o in s)
                        typeof s[o] == "string" && (e.style[o] = s[o])
                }
                return r
            }
            ,
            avalon.fn[t] = function(s) {
                var o = this[0];
                if (arguments.length === 0) {
                    if (o.setTimeout)
                        return o["inner" + e] || o.document.documentElement[n];
                    if (o.nodeType === 9) {
                        var u = o.documentElement;
                        return Math.max(o.body[r], u[r], o.body[i], u[i], u[n])
                    }
                    return Nn[t + "&get"](o)
                }
                return this.css(t, s)
            }
            ,
            avalon.fn["inner" + e] = function() {
                return Nn[t + ":get"](this[0], void 0, -2)
            }
            ,
            avalon.fn["outer" + e] = function(e) {
                return Nn[t + ":get"](this[0], void 0, e === !0 ? 2 : 0)
            }
    }),
        avalon.fn.offset = function() {
            var e = this[0]
                , t = {
                left: 0,
                top: 0
            };
            if (!e || !e.tagName || !e.ownerDocument)
                return t;
            var n = e.ownerDocument
                , r = n.body
                , i = n.documentElement
                , s = n.defaultView || n.parentWindow;
            if (!avalon.contains(i, e))
                return t;
            e.getBoundingClientRect && (t = e.getBoundingClientRect());
            var o = i.clientTop || r.clientTop
                , u = i.clientLeft || r.clientLeft
                , a = Math.max(s.pageYOffset || 0, i.scrollTop, r.scrollTop)
                , f = Math.max(s.pageXOffset || 0, i.scrollLeft, r.scrollLeft);
            return {
                top: t.top + a - o,
                left: t.left + f - u
            }
        }
    ;
    var Fn = /^<option(?:\s+\w+(?:\s*=\s*(?:"[^"]*"|'[^']*'|[^\s>]+))?)*\s+value[\s=]/i
        , In = {
        "option:get": _ ? function(e) {
            return Fn.test(e.outerHTML) ? e.value : e.text.trim()
        }
            : function(e) {
            return e.value
        }
        ,
        "select:get": function(e, t) {
            var n, r = e.options, i = e.selectedIndex, s = In["option:get"], o = e.type === "select-one" || i < 0, u = o ? null : [], a = o ? i + 1 : r.length, f = i < 0 ? a : o ? i : 0;
            for (; f < a; f++) {
                n = r[f];
                if ((n.selected || f === i) && !n.disabled) {
                    t = s(n);
                    if (o)
                        return t;
                    u.push(t)
                }
            }
            return u
        },
        "select:set": function(e, t, n) {
            t = [].concat(t);
            var r = In["option:get"];
            for (var i = 0, s; s = e.options[i++]; )
                if (s.selected = t.indexOf(r(s)) > -1)
                    n = !0;
            n || (e.selectedIndex = -1)
        }
    }
        , qn = {
        "\b": "\\b",
        "	": "\\t",
        "\n": "\\n",
        "\f": "\\f",
        "\r": "\\r",
        '"': '\\"',
        "\\": "\\\\"
    }
        , Rn = e.JSON && JSON.stringify || function(e) {
            return '"' + e.replace(/[\\\"\x00-\x1f]/g, function(e) {
                    var t = qn[e];
                    return typeof t == "string" ? t : "\\u" + ("0000" + e.charCodeAt(0).toString(16)).slice(-4)
                }) + '"'
        }
        , Un = ["break,case,catch,continue,debugger,default,delete,do,else,false", "finally,for,function,if,in,instanceof,new,null,return,switch,this", "throw,true,try,typeof,var,void,while,with", "abstract,boolean,byte,char,class,const,double,enum,export,extends", "final,float,goto,implements,import,int,interface,long,native", "package,private,protected,public,short,static,super,synchronized", "throws,transient,volatile", "arguments,let,yield,undefined"].join(",")
        , zn = /\/\*[\w\W]*?\*\/|\/\/[^\n]*\n|\/\/[^\n]*$|"(?:[^"\\]|\\[\w\W])*"|'(?:[^'\\]|\\[\w\W])*'|[\s\t\n]*\.[\s\t\n]*[$\w\.]+/g
        , Wn = /[^\w$]+/g
        , Xn = new RegExp(["\\b" + Un.replace(/,/g, "\\b|\\b") + "\\b"].join("|"),"g")
        , Vn = /\b\d[^,]*/g
        , $n = /^,+|,+$/g
        , Jn = new R(512)
        , Kn = function(e) {
        var t = "," + e.trim()
            , n = Jn.get(t);
        if (n)
            return n;
        var r = e.replace(zn, "").replace(Wn, ",").replace(Xn, "").replace(Vn, "").replace($n, "").split(/^$|,+/);
        return Jn.put(t, Gn(r))
    }
        , Yn = new R(128)
        , Zn = /\w\[.*\]|\w\.\w/
        , er = /(\$proxy\$[a-z]+)\d+$/
        , tr = /\)\s*$/
        , nr = /\)\s*\|/g
        , rr = /\|\s*([$\w]+)/g
        , ir = /"\s*\["/g
        , sr = /"\s*\(/g;
    avalon.parseExprProxy = fr,
        avalon.scan = function(e, t) {
            e = e || T;
            var n = t ? [].concat(t) : [];
            Dr(e, n)
        }
    ;
    var lr = A("area,base,basefont,br,col,command,embed,hr,img,input,link,meta,param,source,track,wbr,noscript,script,style,textarea".toUpperCase())
        , pr = function(e, t, n) {
        var r = e.getAttribute(t);
        if (r)
            for (var i = 0, s; s = n[i++]; )
                if (s.hasOwnProperty(r) && typeof s[r] == "function")
                    return s[r]
    }
        , vr = _ && e.MutationObserver ? function(e) {
        var t = e.firstChild, n;
        while (t) {
            var r = t.nextSibling;
            t.nodeType === 3 ? n ? (n.nodeValue += t.nodeValue,
                e.removeChild(t)) : n = t : n = null,
                t = r
        }
    }
        : 0
        , mr = /^\s*::/
        , gr = /ms-(\w+)-?(.*)/
        , yr = {
        "if": 10,
        repeat: 90,
        data: 100,
        widget: 110,
        each: 1400,
        "with": 1500,
        duplex: 2e3,
        on: 3e3
    }
        , br = A("animationend,blur,change,input,click,dblclick,focus,keydown,keypress,keyup,mousedown,mouseenter,mouseleave,mousemove,mouseout,mouseover,mouseup,scan,scroll,submit")
        , wr = A("value,title,alt,checked,selected,disabled,readonly,enabled")
        , xr = /^if|widget|repeat$/
        , Tr = /^each|with|html|include$/;
    if (!"1"[0])
        var Nr = new R(512)
            , Cr = /\s+(ms-[^=\s]+)(?:=("[^"]*"|'[^']*'|[^\s>]+))?/g
            , kr = /^['"]/
            , Lr = /<\w+\b(?:(["'])[^"]*?(\1)|[^>])*>/i
            , Ar = /&amp;/g
            , Or = function(e) {
            var t = e.outerHTML;
            if (t.slice(0, 2) === "</" || !t.trim())
                return [];
            var n = t.match(Lr)[0], r = [], i, s, o, u = Nr.get(n);
            if (u)
                return u;
            while (s = Cr.exec(n)) {
                o = s[2],
                o && (o = (kr.test(o) ? o.slice(1, -1) : o).replace(Ar, "&"));
                var a = s[1].toLowerCase();
                i = a.match(gr);
                var f = {
                    name: a,
                    specified: !0,
                    value: o || ""
                };
                r.push(f)
            }
            return Nr.put(n, r)
        };
    var Pr = /\|\s*html(?:\b|$)/
        , Hr = /\|\|/g
        , Br = /&lt;/g
        , jr = /&gt;/g
        , Fr = /(['"])(\\\1|.)+?\1/g
        , Ur = ["autofocus,autoplay,async,allowTransparency,checked,controls", "declare,disabled,defer,defaultChecked,defaultSelected", "contentEditable,isMap,loop,multiple,noHref,noResize,noShade", "open,readOnly,selected"].join(",")
        , zr = {};
    Ur.replace(p, function(e) {
        zr[e.toLowerCase()] = e
    });
    var Wr = {
        "accept-charset": "acceptCharset",
        "char": "ch",
        charoff: "chOff",
        "class": "className",
        "for": "htmlFor",
        "http-equiv": "httpEquiv"
    }
        , Xr = ["accessKey,bgColor,cellPadding,cellSpacing,codeBase,codeType,colSpan", "dateTime,defaultValue,frameBorder,longDesc,maxLength,marginWidth,marginHeight", "rowSpan,tabIndex,useMap,vSpace,valueType,vAlign"].join(",");
    Xr.replace(p, function(e) {
        Wr[e.toLowerCase()] = e
    });
    var Vr = /<noscript.*?>(?:[\s\S]+?)<\/noscript>/img
        , $r = /<noscript.*?>([\s\S]+?)<\/noscript>/im
        , Jr = function() {
        return new (e.XMLHttpRequest || ActiveXObject)("Microsoft.XMLHTTP")
    }
        , Kr = avalon.templateCache = {};
    F.attr = function(e, t) {
        var n = ar(e.value.trim());
        if (e.type === "include") {
            var i = e.element;
            e.includeRendered = pr(i, "data-include-rendered", t),
                e.includeLoaded = pr(i, "data-include-loaded", t);
            var s = e.includeReplace = !!avalon(i).data("includeReplace");
            avalon(i).data("includeCache") && (e.templateCache = {}),
                e.startInclude = r.createComment("ms-include"),
                e.endInclude = r.createComment("ms-include-end"),
                s ? (e.element = e.startInclude,
                    i.parentNode.insertBefore(e.startInclude, i),
                    i.parentNode.insertBefore(e.endInclude, i.nextSibling)) : (i.insertBefore(e.startInclude, i.firstChild),
                    i.appendChild(e.endInclude))
        }
        e.handlerName = "attr",
            fr(n, t, e)
    }
        ,
        I.attr = function(t, n, i) {
            var o = i.type
                , u = i.param;
            if (o === "css")
                avalon(n).css(u, t);
            else if (o === "attr") {
                var a = t === !1 || t === null || t === void 0;
                !x && Wr[u] && (u = Wr[u]);
                var f = zr[u];
                typeof n[f] == "boolean" && (n[f] = !!t,
                t || (a = !0));
                if (a)
                    return n.removeAttribute(u);
                var l = v.test(n) ? !1 : r.namespaces && hn(n) ? !0 : u in n.cloneNode(!1);
                l ? n[u] = t + "" : n.setAttribute(u, t)
            } else if (o === "include" && t) {
                var c = i.vmodels
                    , h = i.includeRendered
                    , p = i.includeLoaded
                    , d = i.includeReplace
                    , m = d ? n.parentNode : n
                    , g = function(e) {
                    if (p) {
                        var n = p.apply(m, [e].concat(c));
                        typeof n == "string" && (e = n)
                    }
                    h && cr(m, function() {
                        h.call(m)
                    }, NaN);
                    var o = i.includeLastID;
                    if (i.templateCache && o && o !== t) {
                        var u = i.templateCache[o];
                        u || (u = i.templateCache[o] = r.createElement("div"),
                            s.appendChild(u))
                    }
                    i.includeLastID = t;
                    for (; ; ) {
                        var a = i.startInclude.nextSibling;
                        if (!a || a === i.endInclude)
                            break;
                        m.removeChild(a),
                        u && u.appendChild(a)
                    }
                    var f = Qr(i, t, e)
                        , l = avalon.slice(f.childNodes);
                    m.insertBefore(f, i.endInclude),
                        _r(l, c)
                };
                if (i.param === "src")
                    if (typeof Kr[t] == "string")
                        avalon.nextTick(function() {
                            g(Kr[t])
                        });
                    else if (Array.isArray(Kr[t]))
                        Kr[t].push(g);
                    else {
                        var y = Jr();
                        y.onreadystatechange = function() {
                            if (y.readyState === 4) {
                                var e = y.status;
                                if (e >= 200 && e < 300 || e === 304 || e === 1223) {
                                    var n = y.responseText;
                                    for (var r = 0, i; i = Kr[t][r++]; )
                                        i(n);
                                    Kr[t] = n
                                }
                            }
                        }
                            ,
                            Kr[t] = [g],
                            y.open("GET", t, !0),
                        "withCredentials"in y && (y.withCredentials = !0),
                            y.setRequestHeader("X-Requested-With", "XMLHttpRequest"),
                            y.send(null)
                    }
                else {
                    var b = t && t.nodeType === 1 ? t : r.getElementById(t);
                    if (b) {
                        if (b.tagName === "NOSCRIPT" && !b.innerHTML && !b.fixIE78) {
                            y = Jr(),
                                y.open("GET", location, !1),
                                y.send(null);
                            var w = r.getElementsByTagName("noscript")
                                , E = (y.responseText || "").match(Vr) || []
                                , S = E.length;
                            for (var N = 0; N < S; N++) {
                                var C = w[N];
                                C && (C.style.display = "none",
                                    C.fixIE78 = (E[N].match($r) || ["", "&nbsp;"])[1])
                            }
                        }
                        avalon.nextTick(function() {
                            g(b.fixIE78 || b.value || b.innerText || b.innerHTML)
                        })
                    }
                }
            } else {
                !T.hasAttribute && typeof t == "string" && (o === "src" || o === "href") && (t = t.replace(/&amp;/g, "&")),
                    n[o] = t;
                if (e.chrome && n.tagName === "EMBED") {
                    var k = n.parentNode
                        , L = document.createComment("ms-src");
                    k.replaceChild(L, n),
                        k.replaceChild(n, L)
                }
            }
        }
        ,
        "title,alt,src,value,css,include,href".replace(p, function(e) {
            F[e] = F.attr
        }),
        F["class"] = function(e, t) {
            var n = e.param, r = e.value, i;
            e.handlerName = "class";
            if (!n || isFinite(n)) {
                e.param = "";
                var s = r.replace(at, function(e) {
                    return e.replace(/./g, "0")
                }).indexOf(":");
                if (s === -1) {
                    var o = r;
                    i = !0
                } else
                    o = r.slice(0, s),
                        i = r.slice(s + 1);
                ut.test(r) ? o = ar(o) : o = Rn(o),
                    e.expr = "[" + o + "," + i + "]"
            } else
                e.expr = "[" + Rn(n) + "," + r + "]",
                    e.oldStyle = n;
            var u = e.type;
            if (u === "hover" || u === "active") {
                if (!e.hasBindEvent) {
                    var a = e.element
                        , f = avalon(a)
                        , l = "mouseenter"
                        , c = "mouseleave";
                    if (u === "active") {
                        a.tabIndex = a.tabIndex || -1,
                            l = "mousedown",
                            c = "mouseup";
                        var h = f.bind("mouseleave", function() {
                            e.toggleClass && f.removeClass(e.newClass)
                        })
                    }
                }
                var p = f.bind(l, function() {
                    e.toggleClass && f.addClass(e.newClass)
                })
                    , d = f.bind(c, function() {
                    e.toggleClass && f.removeClass(e.newClass)
                });
                e.rollback = function() {
                    f.unbind("mouseleave", h),
                        f.unbind(l, p),
                        f.unbind(c, d)
                }
                    ,
                    e.hasBindEvent = !0
            }
            fr(e.expr, t, e)
        }
        ,
        I["class"] = function(e, t, n) {
            var r = avalon(t);
            n.newClass = e[0],
                n.toggleClass = !!e[1],
            n.oldClass && n.newClass !== n.oldClass && r.removeClass(n.oldClass),
                n.oldClass = n.newClass,
            n.type === "class" && (n.oldStyle ? r.toggleClass(n.oldStyle, !!e[1]) : r.toggleClass(n.newClass, n.toggleClass))
        }
        ,
        "hover,active".replace(p, function(e) {
            F[e] = F["class"]
        }),
        I.data = function(e, t, n) {
            var r = "data-" + n.param;
            e && typeof e == "object" ? t[r] = e : t.setAttribute(r, String(e))
        }
    ;
    var Gr = F.duplex = function(e, t) {
            var n = e.element, r;
            fr(e.value, t, e, 1),
                e.changed = pr(n, "data-duplex-changed", t) || L;
            if (e.evaluator && e.args) {
                var i = []
                    , s = A("string,number,boolean,checked");
                n.type === "radio" && e.param === "" && (e.param = "checked"),
                n.msData && (n.msData["ms-duplex"] = e.value),
                    e.param.replace(/\w+/g, function(t) {
                        /^(checkbox|radio)$/.test(n.type) && /^(radio|checked)$/.test(t) && (t === "radio" && u("ms-duplex-radio已经更名为ms-duplex-checked"),
                            t = "checked",
                            e.isChecked = !0),
                            t === "bool" ? (t = "boolean",
                                u("ms-duplex-bool已经更名为ms-duplex-boolean")) : t === "text" && (t = "string",
                                u("ms-duplex-text已经更名为ms-duplex-string")),
                        s[t] && (r = !0),
                            avalon.Array.ensure(i, t)
                    }),
                r || i.push("string"),
                    e.param = i.join("-"),
                    e.bound = function(t, r) {
                        n.addEventListener ? n.addEventListener(t, r, !1) : n.attachEvent("on" + t, r);
                        var i = e.rollback;
                        e.rollback = function() {
                            n.avalonSetter = null,
                                avalon.unbind(n, t, r),
                            i && i()
                        }
                    }
                ;
                for (var o in avalon.vmodels) {
                    var a = avalon.vmodels[o];
                    a.$fire("avalon-ms-duplex-init", e)
                }
                var f = e.pipe || (e.pipe = Zr);
                f(null, e, "init");
                var l = n.tagName;
                Gr[l] && Gr[l](n, e.evaluator.apply(null, e.args), e)
            }
        }
        ;
    avalon.duplexHooks = {
        checked: {
            get: function(e, t) {
                return !t.element.oldValue
            }
        },
        string: {
            get: function(e) {
                return e
            },
            set: Yr
        },
        "boolean": {
            get: function(e) {
                return e === "true"
            },
            set: Yr
        },
        number: {
            get: function(e, t) {
                var n = parseFloat(e);
                if (-e === -n)
                    return n;
                var r = /strong|medium|weak/.exec(t.element.getAttribute("data-duplex-number")) || ["medium"];
                switch (r[0]) {
                    case "strong":
                        return 0;
                    case "medium":
                        return e === "" ? "" : 0;
                    case "weak":
                        return e
                }
            },
            set: Yr
        }
    };
    var ei, ti = [];
    avalon.tick = function(e) {
        ti.push(e) === 1 && (ei = setInterval(ni, 60))
    }
    ;
    var ri = L
        , ii = /text|password|hidden/;
    new function() {
        try {
            var e = {}
                , t = HTMLInputElement.prototype
                , n = HTMLTextAreaElement.prototype;
            function r(t) {
                e[this.tagName].call(this, t),
                ii.test(this.type) && !this.msFocus && this.avalonSetter && this.avalonSetter()
            }
            var i = HTMLInputElement.prototype;
            Object.getOwnPropertyNames(i),
                e.INPUT = Object.getOwnPropertyDescriptor(t, "value").set,
                Object.defineProperty(t, "value", {
                    set: r
                }),
                e.TEXTAREA = Object.getOwnPropertyDescriptor(n, "value").set,
                Object.defineProperty(n, "value", {
                    set: r
                })
        } catch (s) {
            ri = avalon.tick
        }
    }
        ,
    _ && avalon.bind(r, "selectionchange", function(e) {
        var t = r.activeElement;
        t && typeof t.avalonSetter == "function" && t.avalonSetter()
    }),
        Gr.INPUT = function(e, t, n) {
            function a(e) {
                n.changed.call(this, e, n)
            }
            function f() {
                o = !0
            }
            function l() {
                o = !1
            }
            var r = e.type
                , i = n.bound
                , s = avalon(e)
                , o = !1
                , c = function() {
                if (o)
                    return;
                var r = e.oldValue = e.value
                    , i = n.pipe(r, n, "get");
                s.data("duplexObserve") !== !1 && (t(i),
                    a.call(e, i),
                s.data("duplex-focus") && avalon.nextTick(function() {
                    e.focus()
                }))
            };
            n.handler = function() {
                var r = n.pipe(t(), n, "set") + "";
                r !== e.oldValue && (e.value = r)
            }
            ;
            if (n.isChecked || r === "radio") {
                var h = _ === 6;
                c = function() {
                    if (s.data("duplexObserve") !== !1) {
                        var r = n.pipe(e.value, n, "get");
                        t(r),
                            a.call(e, r)
                    }
                }
                    ,
                    n.handler = function() {
                        var r = t()
                            , i = n.isChecked ? !!r : r + "" === e.value;
                        e.oldValue = i,
                            h ? setTimeout(function() {
                                e.defaultChecked = i,
                                    e.checked = i
                            }, 31) : e.checked = i
                    }
                    ,
                    i("click", c)
            } else if (r === "checkbox")
                c = function() {
                    if (s.data("duplexObserve") !== !1) {
                        var r = e.checked ? "ensure" : "remove"
                            , i = t();
                        Array.isArray(i) || (u("ms-duplex应用于checkbox上要对应一个数组"),
                            i = [i]);
                        var o = n.pipe(e.value, n, "get");
                        avalon.Array[r](i, o),
                            a.call(e, i)
                    }
                }
                    ,
                    n.handler = function() {
                        var r = [].concat(t())
                            , i = n.pipe(e.value, n, "get");
                        e.checked = r.indexOf(i) > -1
                    }
                    ,
                    i(x ? "change" : "click", c);
            else {
                var d = e.getAttribute("data-duplex-event") || "input";
                e.attributes["data-event"] && u("data-event指令已经废弃，请改用data-duplex-event");
                function v(e) {
                    setTimeout(function() {
                        c(e)
                    })
                }
                d.replace(p, function(e) {
                    switch (e) {
                        case "input":
                            _ ? (_ > 8 ? i("input", c) : i("propertychange", function(e) {
                                e.propertyName === "value" && c()
                            }),
                                i("dragend", v)) : (i("input", c),
                                i("DOMAutoComplete", c));
                            break;
                        default:
                            i(e, c)
                    }
                }),
                    i("focus", function() {
                        e.msFocus = !0
                    }),
                    i("blur", function() {
                        e.msFocus = !1
                    }),
                ii.test(r) && ri(function() {
                    if (T.contains(e))
                        !e.msFocus && e.oldValue !== e.value && c();
                    else if (!e.msRetain)
                        return !1
                }),
                    e.avalonSetter = c
            }
            e.oldValue = e.value,
                avalon.injectBinding(n),
                a.call(e, e.value)
        }
        ,
        Gr.TEXTAREA = Gr.INPUT,
        Gr.SELECT = function(e, t, n) {
            function i() {
                if (r.data("duplexObserve") !== !1) {
                    var i = r.val();
                    Array.isArray(i) ? i = i.map(function(e) {
                        return n.pipe(e, n, "get")
                    }) : i = n.pipe(i, n, "get"),
                    i + "" !== e.oldValue && t(i),
                        n.changed.call(e, i, n)
                }
            }
            var r = avalon(e);
            n.handler = function() {
                var n = t();
                n = n && n.$model || n,
                    Array.isArray(n) ? e.multiple || u("ms-duplex在<select multiple=true>上要求对应一个数组") : e.multiple && u("ms-duplex在<select multiple=false>不能对应一个数组"),
                    n = Array.isArray(n) ? n.map(String) : n + "",
                n + "" !== e.oldValue && (r.val(n),
                    e.oldValue = n + "")
            }
                ,
                n.bound("change", i),
                e.msCallback = function() {
                    avalon.injectBinding(n),
                        n.changed.call(e, t(), n)
                }
        }
        ,
        I.html = function(e, t, n) {
            var i = t.nodeType !== 1
                , s = i ? t.parentNode : t;
            if (!s)
                return;
            e = e == null ? "" : e;
            if (n.oldText === e)
                return;
            n.oldText = e;
            if (t.nodeType === 3) {
                var o = O("html");
                s.insertBefore(r.createComment(o), t),
                    n.element = r.createComment(o + ":end"),
                    s.replaceChild(n.element, t),
                    t = n.element
            }
            if (typeof e != "object")
                var u = avalon.parseHTML(String(e));
            else if (e.nodeType === 11)
                u = e;
            else if (e.nodeType === 1 || e.item) {
                var a = e.nodeType === 1 ? e.childNodes : e.item;
                u = N.cloneNode(!0);
                while (a[0])
                    u.appendChild(a[0])
            }
            a = avalon.slice(u.childNodes);
            if (i) {
                var f = t.nodeValue.slice(0, -4);
                for (; ; ) {
                    var l = t.previousSibling;
                    if (!l || l.nodeType === 8 && l.nodeValue === f)
                        break;
                    s.removeChild(l)
                }
                s.insertBefore(u, t)
            } else
                avalon.clearHTML(t).appendChild(u);
            _r(a, n.vmodels)
        }
        ,
        F["if"] = F.data = F.text = F.html = function(e, t) {
            fr(e.value, t, e)
        }
        ,
        I["if"] = function(e, t, n) {
            try {
                if (!t.parentNode)
                    return
            } catch (i) {
                return
            }
            if (e)
                t.nodeType === 8 && (t.parentNode.replaceChild(n.template, t),
                    t = n.element = n.template),
                t.getAttribute(n.name) && (t.removeAttribute(n.name),
                    Sr(t, n.vmodels)),
                    n.rollback = null;
            else if (t.nodeType === 1) {
                var o = n.element = r.createComment("ms-if");
                t.parentNode.replaceChild(o, t),
                    n.template = t,
                    s.appendChild(t),
                    n.rollback = function() {
                        t.parentNode === s && s.removeChild(t)
                    }
            }
        }
    ;
    var si = /\(([^)]*)\)/;
    F.on = function(e, t) {
        var n = e.value;
        e.type = "on";
        var r = e.param.replace(/-\d+$/, "");
        typeof F.on[r + "Hook"] == "function" && F.on[r + "Hook"](e);
        if (n.indexOf("(") > 0 && n.indexOf(")") > -1) {
            var i = (n.match(si) || ["", ""])[1].trim();
            if (i === "" || i === "$event")
                n = n.replace(si, "")
        }
        fr(n, t, e)
    }
        ,
        I.on = function(e, t, n) {
            e = function(e) {
                var t = n.evaluator || L;
                return t.apply(this, n.args.concat(e))
            }
            ;
            var r = n.param.replace(/-\d+$/, "");
            if (r === "scan")
                e.call(t, {
                    type: r
                });
            else if (typeof n.specialBind == "function")
                n.specialBind(t, e);
            else
                var i = avalon.bind(t, r, e);
            n.rollback = function() {
                typeof n.specialUnbind == "function" ? n.specialUnbind() : avalon.unbind(t, r, i)
            }
        }
        ,
        F.repeat = function(e, t) {
            var n = e.type;
            fr(e.value, t, e, 1),
                e.proxies = [];
            var i = !1;
            try {
                var s = e.$repeat = e.evaluator.apply(0, e.args || [])
                    , o = avalon.type(s);
                o !== "object" && o !== "array" ? (i = !0,
                    avalon.log("warning:" + e.value + "只能是对象或数组")) : e.xtype = o
            } catch (u) {
                i = !0
            }
            var f = e.value.split(".") || [];
            if (f.length > 1) {
                f.pop();
                var l = f[0];
                for (var c = 0, h; h = t[c++]; )
                    if (h && h.hasOwnProperty(l)) {
                        var p = h[l].$events || {};
                        p[a] = p[a] || [],
                            p[a].push(e);
                        break
                    }
            }
            var d = e.element;
            if (d.nodeType === 1) {
                d.removeAttribute(e.name),
                    e.sortedCallback = pr(d, "data-with-sorted", t),
                    e.renderedCallback = pr(d, "data-" + n + "-rendered", t);
                var v = O(n)
                    , m = r.createComment(v)
                    , g = r.createComment(v + ":end");
                e.signature = v,
                    e.template = N.cloneNode(!1);
                if (n === "repeat") {
                    var y = d.parentNode;
                    y.replaceChild(g, d),
                        y.insertBefore(m, g),
                        e.template.appendChild(d)
                } else {
                    while (d.firstChild)
                        e.template.appendChild(d.firstChild);
                    d.appendChild(m),
                        d.appendChild(g)
                }
                e.element = g,
                    e.handler = I.repeat,
                    e.rollback = function() {
                        var t = e.element;
                        if (!t)
                            return;
                        e.handler("clear")
                    }
            }
            if (i)
                return;
            e.$outer = {};
            var b = "$key"
                , w = "$val";
            Array.isArray(s) && (b = "$first",
                w = "$last");
            for (c = 0; h = t[c++]; )
                if (h.hasOwnProperty(b) && h.hasOwnProperty(w)) {
                    e.$outer = h;
                    break
                }
            var E = s.$events
                , S = (E || {})[a];
            Wt(S, e),
                o === "object" ? e.handler("append") : s.length && e.handler("add", 0, s.length)
        }
        ,
        I.repeat = function(e, t, n) {
            var r = this;
            if (!e && r.xtype) {
                var i = r.$repeat
                    , s = r.evaluator.apply(0, r.args || []);
                if (r.xtype === "array") {
                    if (i.length === s.length)
                        return;
                    e = "add",
                        t = 0,
                        r.$repeat = s,
                        n = s.length
                } else {
                    if (_t(i).join(";;") === _t(s).join(";;"))
                        return;
                    e = "append",
                        r.$repeat = s
                }
            }
            if (e) {
                var o, u, a = r.element, f = ui(r), l = a.parentNode, c = r.proxies, h = N.cloneNode(!1);
                switch (e) {
                    case "add":
                        var p = t + n
                            , d = [];
                        for (var v = t; v < p; v++) {
                            var m = mi(v, r);
                            c.splice(v, 0, m),
                                oi(r, h, m, d)
                        }
                        l.insertBefore(h, f[t] || a);
                        for (v = 0; u = d[v++]; )
                            _r(u.nodes, u.vmodels),
                                u.nodes = u.vmodels = null;
                        break;
                    case "del":
                        ai(f[t], f[t + n] || a);
                        var g = c.splice(t, n);
                        hi(g, "each");
                        break;
                    case "clear":
                        o = f[0],
                        o && (ai(o, a),
                            r.xtype === "object" ? l.insertBefore(o, a) : hi(c, "each"));
                        break;
                    case "move":
                        o = f[0];
                        if (o) {
                            var y = o.nodeValue, b = [], w = [], E;
                            ai(o, a, function() {
                                w.unshift(this),
                                this.nodeValue === y && (b.unshift(w),
                                    w = [])
                            }),
                                Rt(b, t),
                                Rt(c, t);
                            while (w = b.shift())
                                while (E = w.shift())
                                    h.appendChild(E);
                            l.insertBefore(h, a)
                        }
                        break;
                    case "index":
                        var S = c.length - 1;
                        for (; n = c[t]; t++)
                            n.$index = t,
                                n.$first = t === 0,
                                n.$last = t === S;
                        return;
                    case "set":
                        m = c[t],
                        m && Xt(m.$events[r.param || "el"]);
                        break;
                    case "append":
                        var x = r.$repeat
                            , T = Array.isArray(c) || !c ? {} : c;
                        r.proxies = T;
                        var C = [];
                        d = [];
                        for (var k in T)
                            x.hasOwnProperty(k) || (gi(T[k], fi),
                                delete T[k]);
                        for (k in x)
                            x.hasOwnProperty(k) && k !== "hasOwnProperty" && C.push(k);
                        if (r.sortedCallback) {
                            var A = r.sortedCallback.call(l, C);
                            A && Array.isArray(A) && A.length && (C = A)
                        }
                        for (v = 0; k = C[v++]; )
                            k !== "hasOwnProperty" && (T[k] = ci(T[k], k, r),
                                oi(r, h, T[k], d));
                        l.insertBefore(h, a);
                        for (v = 0; u = d[v++]; )
                            _r(u.nodes, u.vmodels),
                                u.nodes = u.vmodels = null
                }
                if (!r.$repeat || r.$repeat.hasOwnProperty("$lock"))
                    return;
                e === "clear" && (e = "del");
                var O = r.renderedCallback || L
                    , M = arguments;
                l.oldValue && l.tagName === "SELECT" && avalon(l).val(l.oldValue.split(",")),
                    O.apply(l, M)
            }
        }
        ,
        "with,each".replace(p, function(e) {
            F[e] = F.repeat
        });
    var fi = []
        , di = [];
    I.text = function(e, t) {
        e = e == null ? "" : e;
        if (t.nodeType === 3)
            try {
                t.data = e
            } catch (n) {}
        else
            "textContent"in t ? t.textContent = e : t.innerText = e
    }
        ,
        avalon.parseDisplay = yi,
        F.visible = function(e, t) {
            fr(e.value, t, e)
        }
        ,
        I.visible = function(e, t, n) {
            e ? (t.style.display = n.display || "",
            avalon(t).css("display") === "none" && (t.style.display = n.display = yi(t.nodeName))) : t.style.display = "none"
        }
        ,
        F.widget = function(t, n) {
            var r = t.value.match(p)
                , i = t.element
                , s = r[0]
                , o = r[1];
            if (!o || o === "$")
                o = O(s);
            var a = r[2] || s
                , f = avalon.ui[s];
            if (typeof f == "function") {
                n = i.vmodels || n;
                for (var l = 0, c; c = n[l++]; )
                    if (c.hasOwnProperty(a) && typeof c[a] == "object") {
                        var h = c[a];
                        h = h.$model || h;
                        break
                    }
                if (h) {
                    var d = h[s + "Id"];
                    typeof d == "string" && (u("warning!不再支持" + s + "Id"),
                        o = d)
                }
                var v = avalon.getWidgetData(i, s);
                t.value = [s, o, a].join(","),
                    t[s + "Id"] = o,
                    t.evaluator = L,
                    i.msData["ms-widget-id"] = o;
                var m = t[s + "Options"] = avalon.mix({}, f.defaults, h || {}, v);
                i.removeAttribute("ms-widget");
                var g = f(i, t, n) || {};
                if (g.$id) {
                    avalon.vmodels[o] = g,
                        hr(i, g);
                    try {
                        g.$init(function() {
                            avalon.scan(i, [g].concat(n)),
                            typeof m.onInit == "function" && m.onInit.call(i, g, m, n)
                        })
                    } catch (y) {}
                    t.rollback = function() {
                        try {
                            g.widgetElement = null,
                                g.$remove()
                        } catch (e) {}
                        i.msData = {},
                            delete avalon.vmodels[g.$id]
                    }
                        ,
                        Zt(t, bi),
                    e.chrome && i.addEventListener("DOMNodeRemovedFromDocument", function() {
                        setTimeout(en)
                    })
                } else
                    avalon.scan(i, n)
            } else
                n.length && (i.vmodels = n)
        }
    ;
    var bi = []
        , wi = /<script[^>]*>([\S\s]*?)<\/script\s*>/gim
        , Ei = /\s+(on[^=\s]+)(?:=("[^"]*"|'[^']*'|[^\s>]+))?/g
        , Si = /<\w+\b(?:(["'])[^"]*?(\1)|[^>])*>/ig
        , xi = {
        a: /\b(href)\=("javascript[^"]*"|'javascript[^']*')/ig,
        img: /\b(src)\=("javascript[^"]*"|'javascript[^']*')/ig,
        form: /\b(action)\=("javascript[^"]*"|'javascript[^']*')/ig
    }
        , Ti = /[\uD800-\uDBFF][\uDC00-\uDFFF]/g
        , Ni = /([^\#-~| |!])/g
        , ki = avalon.filters = {
        uppercase: function(e) {
            return e.toUpperCase()
        },
        lowercase: function(e) {
            return e.toLowerCase()
        },
        truncate: function(e, t, n) {
            return t = t || 30,
                n = typeof n == "string" ? n : "...",
                e.length > t ? e.slice(0, t - n.length) + n : String(e)
        },
        $filter: function(e) {
            for (var t = 1, n = arguments.length; t < n; t++) {
                var r = arguments[t]
                    , i = avalon.filters[r.shift()];
                if (typeof i == "function") {
                    var s = [e].concat(r);
                    e = i.apply(null, s)
                }
            }
            return e
        },
        camelize: vn,
        sanitize: function(e) {
            return e.replace(wi, "").replace(Si, function(e, t) {
                var n = e.toLowerCase().match(/<(\w+)\s/);
                if (n) {
                    var r = xi[n[1]];
                    r && (e = e.replace(r, function(e, t, n) {
                        var r = n.charAt(0);
                        return t + "=" + r + "javascript:void(0)" + r
                    }))
                }
                return e.replace(Ei, " ").replace(/\s+/g, " ")
            })
        },
        escape: function(e) {
            return String(e).replace(/&/g, "&amp;").replace(Ti, function(e) {
                var t = e.charCodeAt(0)
                    , n = e.charCodeAt(1);
                return "&#" + ((t - 55296) * 1024 + (n - 56320) + 65536) + ";"
            }).replace(Ni, function(e) {
                return "&#" + e.charCodeAt(0) + ";"
            }).replace(/</g, "&lt;").replace(/>/g, "&gt;")
        },
        currency: function(e, t, n) {
            return (t || "￥") + Ci(e, isFinite(n) ? n : 2)
        },
        number: Ci
    };
    new function() {
        function e(e) {
            return parseInt(e, 10) || 0
        }
        function t(e, t, n) {
            var r = "";
            e < 0 && (r = "-",
                e = -e),
                e = "" + e;
            while (e.length < t)
                e = "0" + e;
            return n && (e = e.substr(e.length - t)),
            r + e
        }
        function n(e, n, r, i) {
            return function(s) {
                var o = s["get" + e]();
                if (r > 0 || o > -r)
                    o += r;
                return o === 0 && r === -12 && (o = 12),
                    t(o, n, i)
            }
        }
        function r(e, t) {
            return function(n, r) {
                var i = n["get" + e]()
                    , s = (t ? "SHORT" + e : e).toUpperCase();
                return r[s][i]
            }
        }
        function i(e) {
            var n = -1 * e.getTimezoneOffset()
                , r = n >= 0 ? "+" : "";
            return r += t(Math[n > 0 ? "floor" : "ceil"](n / 60), 2) + t(Math.abs(n % 60), 2),
                r
        }
        function s(e, t) {
            return e.getHours() < 12 ? t.AMPMS[0] : t.AMPMS[1]
        }
        var o = {
            yyyy: n("FullYear", 4),
            yy: n("FullYear", 2, 0, !0),
            y: n("FullYear", 1),
            MMMM: r("Month"),
            MMM: r("Month", !0),
            MM: n("Month", 2, 1),
            M: n("Month", 1, 1),
            dd: n("Date", 2),
            d: n("Date", 1),
            HH: n("Hours", 2),
            H: n("Hours", 1),
            hh: n("Hours", 2, -12),
            h: n("Hours", 1, -12),
            mm: n("Minutes", 2),
            m: n("Minutes", 1),
            ss: n("Seconds", 2),
            s: n("Seconds", 1),
            sss: n("Milliseconds", 3),
            EEEE: r("Day"),
            EEE: r("Day", !0),
            a: s,
            Z: i
        }
            , u = /((?:[^yMdHhmsaZE']+)|(?:'(?:[^']|'')*')|(?:E+|y+|M+|d+|H+|h+|m+|s+|a|Z))(.*)/
            , a = /^\/Date\((\d+)\)\/$/;
        ki.date = function(t, n) {
            var r = ki.date.locate, i = "", s = [], f, l;
            n = n || "mediumDate",
                n = r[n] || n;
            if (typeof t == "string")
                if (/^\d+$/.test(t))
                    t = e(t);
                else if (a.test(t))
                    t = +RegExp.$1;
                else {
                    var c = t.trim()
                        , h = [0, 0, 0, 0, 0, 0, 0]
                        , p = new Date(0);
                    c = c.replace(/^(\d+)\D(\d+)\D(\d+)/, function(t, n, r, i) {
                        var s = i.length === 4 ? [i, n, r] : [n, r, i];
                        return h[0] = e(s[0]),
                            h[1] = e(s[1]) - 1,
                            h[2] = e(s[2]),
                            ""
                    });
                    var d = p.setFullYear
                        , v = p.setHours;
                    c = c.replace(/[T\s](\d+):(\d+):?(\d+)?\.?(\d)?/, function(t, n, r, i, s) {
                        return h[3] = e(n),
                            h[4] = e(r),
                            h[5] = e(i),
                        s && (h[6] = Math.round(parseFloat("0." + s) * 1e3)),
                            ""
                    });
                    var m = 0
                        , g = 0;
                    c = c.replace(/Z|([+-])(\d\d):?(\d\d)/, function(t, n, r, i) {
                        return d = p.setUTCFullYear,
                            v = p.setUTCHours,
                        n && (m = e(n + r),
                            g = e(n + i)),
                            ""
                    }),
                        h[3] -= m,
                        h[4] -= g,
                        d.apply(p, h.slice(0, 3)),
                        v.apply(p, h.slice(3)),
                        t = p
                }
            typeof t == "number" && (t = new Date(t));
            if (avalon.type(t) !== "date")
                return;
            while (n)
                l = u.exec(n),
                    l ? (s = s.concat(l.slice(1)),
                        n = s.pop()) : (s.push(n),
                        n = null);
            return s.forEach(function(e) {
                f = o[e],
                    i += f ? f(t, r) : e.replace(/(^'|'$)/g, "").replace(/''/g, "'")
            }),
                i
        }
        ;
        var f = {
            AMPMS: {
                0: "上午",
                1: "下午"
            },
            DAY: {
                0: "星期日",
                1: "星期一",
                2: "星期二",
                3: "星期三",
                4: "星期四",
                5: "星期五",
                6: "星期六"
            },
            MONTH: {
                0: "1月",
                1: "2月",
                2: "3月",
                3: "4月",
                4: "5月",
                5: "6月",
                6: "7月",
                7: "8月",
                8: "9月",
                9: "10月",
                10: "11月",
                11: "12月"
            },
            SHORTDAY: {
                0: "周日",
                1: "周一",
                2: "周二",
                3: "周三",
                4: "周四",
                5: "周五",
                6: "周六"
            },
            fullDate: "y年M月d日EEEE",
            longDate: "y年M月d日",
            medium: "yyyy-M-d H:mm:ss",
            mediumDate: "yyyy-M-d",
            mediumTime: "H:mm:ss",
            "short": "yy-M-d ah:mm",
            shortDate: "yy-M-d",
            shortTime: "ah:mm"
        };
        f.SHORTMONTH = f.MONTH,
            ki.date.locate = f
    }
        ,
        new function() {
            function i(n) {
                t = 1;
                while (n = e.shift())
                    n()
            }
            avalon.config({
                loader: !1
            });
            var e = [], t = r.readyState === "complete", n;
            avalon.bind(r, "DOMContentLoaded", n = function() {
                    avalon.unbind(r, "DOMContentLoaded", n),
                        i()
                }
            );
            var s = setInterval(function() {
                document.readyState === "complete" && document.body && (clearInterval(s),
                    i())
            }, 50);
            avalon.ready = function(n) {
                t ? n(avalon) : e.push(n)
            }
                ,
                avalon.ready(function() {
                    avalon.scan(r.body)
                })
        }
        ,
    typeof define == "function" && define.amd && define("avalon", [], function() {
        return avalon
    });
    var Li = e.avalon;
    return avalon.noConflict = function(t) {
        return t && e.avalon === avalon && (e.avalon = Li),
            avalon
    }
        ,
    t === void 0 && (e.avalon = avalon),
        avalon
}),
    function(e, t) {
        function P(e) {
            var t = e.length
                , n = b.type(e);
            return b.isWindow(e) ? !1 : 1 === e.nodeType && t ? !0 : "array" === n || "function" !== n && (0 === t || "number" == typeof t && t > 0 && t - 1 in e)
        }
        function B(e) {
            var t = H[e] = {};
            return b.each(e.match(E) || [], function(e, n) {
                t[n] = !0
            }),
                t
        }
        function I(e, n, r, i) {
            if (b.acceptData(e)) {
                var s, o, u = b.expando, a = "string" == typeof n, f = e.nodeType, c = f ? b.cache : e, h = f ? e[u] : e[u] && u;
                if (h && c[h] && (i || c[h].data) || !a || r !== t)
                    return h || (f ? e[u] = h = l.pop() || b.guid++ : h = u),
                    c[h] || (c[h] = {},
                    f || (c[h].toJSON = b.noop)),
                    ("object" == typeof n || "function" == typeof n) && (i ? c[h] = b.extend(c[h], n) : c[h].data = b.extend(c[h].data, n)),
                        s = c[h],
                    i || (s.data || (s.data = {}),
                        s = s.data),
                    r !== t && (s[b.camelCase(n)] = r),
                        a ? (o = s[n],
                        null == o && (o = s[b.camelCase(n)])) : o = s,
                        o
            }
        }
        function q(e, t, n) {
            if (b.acceptData(e)) {
                var r, i, s, o = e.nodeType, u = o ? b.cache : e, a = o ? e[b.expando] : b.expando;
                if (u[a]) {
                    if (t && (s = n ? u[a] : u[a].data)) {
                        b.isArray(t) ? t = t.concat(b.map(t, b.camelCase)) : t in s ? t = [t] : (t = b.camelCase(t),
                            t = t in s ? [t] : t.split(" "));
                        for (r = 0,
                                 i = t.length; i > r; r++)
                            delete s[t[r]];
                        if (!(n ? U : b.isEmptyObject)(s))
                            return
                    }
                    (n || (delete u[a].data,
                        U(u[a]))) && (o ? b.cleanData([e], !0) : b.support.deleteExpando || u != u.window ? delete u[a] : u[a] = null)
                }
            }
        }
        function R(e, n, r) {
            if (r === t && 1 === e.nodeType) {
                var i = "data-" + n.replace(F, "-$1").toLowerCase();
                if (r = e.getAttribute(i),
                    "string" == typeof r) {
                    try {
                        r = "true" === r ? !0 : "false" === r ? !1 : "null" === r ? null : +r + "" === r ? +r : j.test(r) ? b.parseJSON(r) : r
                    } catch (s) {}
                    b.data(e, n, r)
                } else
                    r = t
            }
            return r
        }
        function U(e) {
            var t;
            for (t in e)
                if (("data" !== t || !b.isEmptyObject(e[t])) && "toJSON" !== t)
                    return !1;
            return !0
        }
        function it() {
            return !0
        }
        function st() {
            return !1
        }
        function ct(e, t) {
            do
                e = e[t];
            while (e && 1 !== e.nodeType);return e
        }
        function ht(e, t, n) {
            if (t = t || 0,
                    b.isFunction(t))
                return b.grep(e, function(e, r) {
                    var i = !!t.call(e, r, e);
                    return i === n
                });
            if (t.nodeType)
                return b.grep(e, function(e) {
                    return e === t === n
                });
            if ("string" == typeof t) {
                var r = b.grep(e, function(e) {
                    return 1 === e.nodeType
                });
                if (at.test(t))
                    return b.filter(t, r, !n);
                t = b.filter(t, r)
            }
            return b.grep(e, function(e) {
                return b.inArray(e, t) >= 0 === n
            })
        }
        function pt(e) {
            var t = dt.split("|")
                , n = e.createDocumentFragment();
            if (n.createElement)
                while (t.length)
                    n.createElement(t.pop());
            return n
        }
        function Mt(e, t) {
            return e.getElementsByTagName(t)[0] || e.appendChild(e.ownerDocument.createElement(t))
        }
        function _t(e) {
            var t = e.getAttributeNode("type");
            return e.type = (t && t.specified) + "/" + e.type,
                e
        }
        function Dt(e) {
            var t = Ct.exec(e.type);
            return t ? e.type = t[1] : e.removeAttribute("type"),
                e
        }
        function Pt(e, t) {
            var n, r = 0;
            for (; null != (n = e[r]); r++)
                b._data(n, "globalEval", !t || b._data(t[r], "globalEval"))
        }
        function Ht(e, t) {
            if (1 === t.nodeType && b.hasData(e)) {
                var n, r, i, s = b._data(e), o = b._data(t, s), u = s.events;
                if (u) {
                    delete o.handle,
                        o.events = {};
                    for (n in u)
                        for (r = 0,
                                 i = u[n].length; i > r; r++)
                            b.event.add(t, n, u[n][r])
                }
                o.data && (o.data = b.extend({}, o.data))
            }
        }
        function Bt(e, t) {
            var n, r, i;
            if (1 === t.nodeType) {
                if (n = t.nodeName.toLowerCase(),
                    !b.support.noCloneEvent && t[b.expando]) {
                    i = b._data(t);
                    for (r in i.events)
                        b.removeEvent(t, r, i.handle);
                    t.removeAttribute(b.expando)
                }
                "script" === n && t.text !== e.text ? (_t(t).text = e.text,
                    Dt(t)) : "object" === n ? (t.parentNode && (t.outerHTML = e.outerHTML),
                b.support.html5Clone && e.innerHTML && !b.trim(t.innerHTML) && (t.innerHTML = e.innerHTML)) : "input" === n && xt.test(e.type) ? (t.defaultChecked = t.checked = e.checked,
                t.value !== e.value && (t.value = e.value)) : "option" === n ? t.defaultSelected = t.selected = e.defaultSelected : ("input" === n || "textarea" === n) && (t.defaultValue = e.defaultValue)
            }
        }
        function jt(e, n) {
            var r, s, o = 0, u = typeof e.getElementsByTagName !== i ? e.getElementsByTagName(n || "*") : typeof e.querySelectorAll !== i ? e.querySelectorAll(n || "*") : t;
            if (!u)
                for (u = [],
                         r = e.childNodes || e; null != (s = r[o]); o++)
                    !n || b.nodeName(s, n) ? u.push(s) : b.merge(u, jt(s, n));
            return n === t || n && b.nodeName(e, n) ? b.merge([e], u) : u
        }
        function Ft(e) {
            xt.test(e.type) && (e.defaultChecked = e.checked)
        }
        function tn(e, t) {
            if (t in e)
                return t;
            var n = t.charAt(0).toUpperCase() + t.slice(1)
                , r = t
                , i = en.length;
            while (i--)
                if (t = en[i] + n,
                    t in e)
                    return t;
            return r
        }
        function nn(e, t) {
            return e = t || e,
            "none" === b.css(e, "display") || !b.contains(e.ownerDocument, e)
        }
        function rn(e, t) {
            var n, r, i, s = [], o = 0, u = e.length;
            for (; u > o; o++)
                r = e[o],
                r.style && (s[o] = b._data(r, "olddisplay"),
                    n = r.style.display,
                    t ? (s[o] || "none" !== n || (r.style.display = ""),
                    "" === r.style.display && nn(r) && (s[o] = b._data(r, "olddisplay", an(r.nodeName)))) : s[o] || (i = nn(r),
                    (n && "none" !== n || !i) && b._data(r, "olddisplay", i ? n : b.css(r, "display"))));
            for (o = 0; u > o; o++)
                r = e[o],
                r.style && (t && "none" !== r.style.display && "" !== r.style.display || (r.style.display = t ? s[o] || "" : "none"));
            return e
        }
        function sn(e, t, n) {
            var r = $t.exec(t);
            return r ? Math.max(0, r[1] - (n || 0)) + (r[2] || "px") : t
        }
        function on(e, t, n, r, i) {
            var s = n === (r ? "border" : "content") ? 4 : "width" === t ? 1 : 0
                , o = 0;
            for (; 4 > s; s += 2)
                "margin" === n && (o += b.css(e, n + Zt[s], !0, i)),
                    r ? ("content" === n && (o -= b.css(e, "padding" + Zt[s], !0, i)),
                    "margin" !== n && (o -= b.css(e, "border" + Zt[s] + "Width", !0, i))) : (o += b.css(e, "padding" + Zt[s], !0, i),
                    "padding" !== n && (o += b.css(e, "border" + Zt[s] + "Width", !0, i)));
            return o
        }
        function un(e, t, n) {
            var r = !0
                , i = "width" === t ? e.offsetWidth : e.offsetHeight
                , s = qt(e)
                , o = b.support.boxSizing && "border-box" === b.css(e, "boxSizing", !1, s);
            if (0 >= i || null == i) {
                if (i = Rt(e, t, s),
                    (0 > i || null == i) && (i = e.style[t]),
                        Jt.test(i))
                    return i;
                r = o && (b.support.boxSizingReliable || i === e.style[t]),
                    i = parseFloat(i) || 0
            }
            return i + on(e, t, n || (o ? "border" : "content"), r, s) + "px"
        }
        function an(e) {
            var t = s
                , n = Qt[e];
            return n || (n = fn(e, t),
            "none" !== n && n || (It = (It || b("<iframe frameborder='0' width='0' height='0'/>").css("cssText", "display:block !important")).appendTo(t.documentElement),
                t = (It[0].contentWindow || It[0].contentDocument).document,
                t.write("<!doctype html><html><body>"),
                t.close(),
                n = fn(e, t),
                It.detach()),
                Qt[e] = n),
                n
        }
        function fn(e, t) {
            var n = b(t.createElement(e)).appendTo(t.body)
                , r = b.css(n[0], "display");
            return n.remove(),
                r
        }
        function vn(e, t, n, r) {
            var i;
            if (b.isArray(t))
                b.each(t, function(t, i) {
                    n || cn.test(e) ? r(e, i) : vn(e + "[" + ("object" == typeof i ? t : "") + "]", i, n, r)
                });
            else if (n || "object" !== b.type(t))
                r(e, t);
            else
                for (i in t)
                    vn(e + "[" + i + "]", t[i], n, r)
        }
        function _n(e) {
            return function(t, n) {
                "string" != typeof t && (n = t,
                    t = "*");
                var r, i = 0, s = t.toLowerCase().match(E) || [];
                if (b.isFunction(n))
                    while (r = s[i++])
                        "+" === r[0] ? (r = r.slice(1) || "*",
                            (e[r] = e[r] || []).unshift(n)) : (e[r] = e[r] || []).push(n)
            }
        }
        function Dn(e, n, r, i) {
            function u(a) {
                var f;
                return s[a] = !0,
                    b.each(e[a] || [], function(e, a) {
                        var l = a(n, r, i);
                        return "string" != typeof l || o || s[l] ? o ? !(f = l) : t : (n.dataTypes.unshift(l),
                            u(l),
                            !1)
                    }),
                    f
            }
            var s = {}
                , o = e === An;
            return u(n.dataTypes[0]) || !s["*"] && u("*")
        }
        function Pn(e, n) {
            var r, i, s = b.ajaxSettings.flatOptions || {};
            for (i in n)
                n[i] !== t && ((s[i] ? e : r || (r = {}))[i] = n[i]);
            return r && b.extend(!0, e, r),
                e
        }
        function Hn(e, n, r) {
            var i, s, o, u, a = e.contents, f = e.dataTypes, l = e.responseFields;
            for (u in l)
                u in r && (n[l[u]] = r[u]);
            while ("*" === f[0])
                f.shift(),
                s === t && (s = e.mimeType || n.getResponseHeader("Content-Type"));
            if (s)
                for (u in a)
                    if (a[u] && a[u].test(s)) {
                        f.unshift(u);
                        break
                    }
            if (f[0]in r)
                o = f[0];
            else {
                for (u in r) {
                    if (!f[0] || e.converters[u + " " + f[0]]) {
                        o = u;
                        break
                    }
                    i || (i = u)
                }
                o = o || i
            }
            return o ? (o !== f[0] && f.unshift(o),
                r[o]) : t
        }
        function Bn(e, t) {
            var n, r, i, s, o = {}, u = 0, a = e.dataTypes.slice(), f = a[0];
            if (e.dataFilter && (t = e.dataFilter(t, e.dataType)),
                    a[1])
                for (i in e.converters)
                    o[i.toLowerCase()] = e.converters[i];
            for (; r = a[++u]; )
                if ("*" !== r) {
                    if ("*" !== f && f !== r) {
                        if (i = o[f + " " + r] || o["* " + r],
                                !i)
                            for (n in o)
                                if (s = n.split(" "),
                                    s[1] === r && (i = o[f + " " + s[0]] || o["* " + s[0]])) {
                                    i === !0 ? i = o[n] : o[n] !== !0 && (r = s[0],
                                        a.splice(u--, 0, r));
                                    break
                                }
                        if (i !== !0)
                            if (i && e["throws"])
                                t = i(t);
                            else
                                try {
                                    t = i(t)
                                } catch (l) {
                                    return {
                                        state: "parsererror",
                                        error: i ? l : "No conversion from " + f + " to " + r
                                    }
                                }
                    }
                    f = r
                }
            return {
                state: "success",
                data: t
            }
        }
        function zn() {
            try {
                return new e.XMLHttpRequest
            } catch (t) {}
        }
        function Wn() {
            try {
                return new e.ActiveXObject("Microsoft.XMLHTTP")
            } catch (t) {}
        }
        function Yn() {
            return setTimeout(function() {
                Xn = t
            }),
                Xn = b.now()
        }
        function Zn(e, t) {
            b.each(t, function(t, n) {
                var r = (Gn[t] || []).concat(Gn["*"])
                    , i = 0
                    , s = r.length;
                for (; s > i; i++)
                    if (r[i].call(e, t, n))
                        return
            })
        }
        function er(e, t, n) {
            var r, i, s = 0, o = Qn.length, u = b.Deferred().always(function() {
                delete a.elem
            }), a = function() {
                if (i)
                    return !1;
                var t = Xn || Yn()
                    , n = Math.max(0, f.startTime + f.duration - t)
                    , r = n / f.duration || 0
                    , s = 1 - r
                    , o = 0
                    , a = f.tweens.length;
                for (; a > o; o++)
                    f.tweens[o].run(s);
                return u.notifyWith(e, [f, s, n]),
                    1 > s && a ? n : (u.resolveWith(e, [f]),
                        !1)
            }, f = u.promise({
                elem: e,
                props: b.extend({}, t),
                opts: b.extend(!0, {
                    specialEasing: {}
                }, n),
                originalProperties: t,
                originalOptions: n,
                startTime: Xn || Yn(),
                duration: n.duration,
                tweens: [],
                createTween: function(t, n) {
                    var r = b.Tween(e, f.opts, t, n, f.opts.specialEasing[t] || f.opts.easing);
                    return f.tweens.push(r),
                        r
                },
                stop: function(t) {
                    var n = 0
                        , r = t ? f.tweens.length : 0;
                    if (i)
                        return this;
                    for (i = !0; r > n; n++)
                        f.tweens[n].run(1);
                    return t ? u.resolveWith(e, [f, t]) : u.rejectWith(e, [f, t]),
                        this
                }
            }), l = f.props;
            for (tr(l, f.opts.specialEasing); o > s; s++)
                if (r = Qn[s].call(f, e, l, f.opts))
                    return r;
            return Zn(f, l),
            b.isFunction(f.opts.start) && f.opts.start.call(e, f),
                b.fx.timer(b.extend(a, {
                    elem: e,
                    anim: f,
                    queue: f.opts.queue
                })),
                f.progress(f.opts.progress).done(f.opts.done, f.opts.complete).fail(f.opts.fail).always(f.opts.always)
        }
        function tr(e, t) {
            var n, r, i, s, o;
            for (i in e)
                if (r = b.camelCase(i),
                        s = t[r],
                        n = e[i],
                    b.isArray(n) && (s = n[1],
                        n = e[i] = n[0]),
                    i !== r && (e[r] = n,
                        delete e[i]),
                        o = b.cssHooks[r],
                    o && "expand"in o) {
                    n = o.expand(n),
                        delete e[r];
                    for (i in n)
                        i in e || (e[i] = n[i],
                            t[i] = s)
                } else
                    t[r] = s
        }
        function nr(e, t, n) {
            var r, i, s, o, u, a, f, l, c, h = this, p = e.style, d = {}, v = [], m = e.nodeType && nn(e);
            n.queue || (l = b._queueHooks(e, "fx"),
            null == l.unqueued && (l.unqueued = 0,
                    c = l.empty.fire,
                    l.empty.fire = function() {
                        l.unqueued || c()
                    }
            ),
                l.unqueued++,
                h.always(function() {
                    h.always(function() {
                        l.unqueued--,
                        b.queue(e, "fx").length || l.empty.fire()
                    })
                })),
            1 === e.nodeType && ("height"in t || "width"in t) && (n.overflow = [p.overflow, p.overflowX, p.overflowY],
            "inline" === b.css(e, "display") && "none" === b.css(e, "float") && (b.support.inlineBlockNeedsLayout && "inline" !== an(e.nodeName) ? p.zoom = 1 : p.display = "inline-block")),
            n.overflow && (p.overflow = "hidden",
            b.support.shrinkWrapBlocks || h.always(function() {
                p.overflow = n.overflow[0],
                    p.overflowX = n.overflow[1],
                    p.overflowY = n.overflow[2]
            }));
            for (i in t)
                if (o = t[i],
                        $n.exec(o)) {
                    if (delete t[i],
                            a = a || "toggle" === o,
                        o === (m ? "hide" : "show"))
                        continue;
                    v.push(i)
                }
            if (s = v.length) {
                u = b._data(e, "fxshow") || b._data(e, "fxshow", {}),
                "hidden"in u && (m = u.hidden),
                a && (u.hidden = !m),
                    m ? b(e).show() : h.done(function() {
                        b(e).hide()
                    }),
                    h.done(function() {
                        var t;
                        b._removeData(e, "fxshow");
                        for (t in d)
                            b.style(e, t, d[t])
                    });
                for (i = 0; s > i; i++)
                    r = v[i],
                        f = h.createTween(r, m ? u[r] : 0),
                        d[r] = u[r] || b.style(e, r),
                    r in u || (u[r] = f.start,
                    m && (f.end = f.start,
                        f.start = "width" === r || "height" === r ? 1 : 0))
            }
        }
        function rr(e, t, n, r, i) {
            return new rr.prototype.init(e,t,n,r,i)
        }
        function ir(e, t) {
            var n, r = {
                height: e
            }, i = 0;
            for (t = t ? 1 : 0; 4 > i; i += 2 - t)
                n = Zt[i],
                    r["margin" + n] = r["padding" + n] = e;
            return t && (r.opacity = r.width = e),
                r
        }
        function sr(e) {
            return b.isWindow(e) ? e : 9 === e.nodeType ? e.defaultView || e.parentWindow : !1
        }
        var n, r, i = typeof t, s = e.document, o = e.location, u = e.jQuery, a = e.$, f = {}, l = [], c = "1.9.1", h = l.concat, p = l.push, d = l.slice, v = l.indexOf, m = f.toString, g = f.hasOwnProperty, y = c.trim, b = function(e, t) {
            return new b.fn.init(e,t,r)
        }, w = /[+-]?(?:\d*\.|)\d+(?:[eE][+-]?\d+|)/.source, E = /\S+/g, S = /^[\s\uFEFF\xA0]+|[\s\uFEFF\xA0]+$/g, x = /^(?:(<[\w\W]+>)[^>]*|#([\w-]*))$/, T = /^<(\w+)\s*\/?>(?:<\/\1>|)$/, N = /^[\],:{}\s]*$/, C = /(?:^|:|,)(?:\s*\[)+/g, k = /\\(?:["\\\/bfnrt]|u[\da-fA-F]{4})/g, L = /"[^"\\\r\n]*"|true|false|null|-?(?:\d+\.|)\d+(?:[eE][+-]?\d+|)/g, A = /^-ms-/, O = /-([\da-z])/gi, M = function(e, t) {
            return t.toUpperCase()
        }, _ = function(e) {
            (s.addEventListener || "load" === e.type || "complete" === s.readyState) && (D(),
                b.ready())
        }, D = function() {
            s.addEventListener ? (s.removeEventListener("DOMContentLoaded", _, !1),
                e.removeEventListener("load", _, !1)) : (s.detachEvent("onreadystatechange", _),
                e.detachEvent("onload", _))
        };
        b.fn = b.prototype = {
            jquery: c,
            constructor: b,
            init: function(e, n, r) {
                var i, o;
                if (!e)
                    return this;
                if ("string" == typeof e) {
                    if (i = "<" === e.charAt(0) && ">" === e.charAt(e.length - 1) && e.length >= 3 ? [null, e, null] : x.exec(e),
                        !i || !i[1] && n)
                        return !n || n.jquery ? (n || r).find(e) : this.constructor(n).find(e);
                    if (i[1]) {
                        if (n = n instanceof b ? n[0] : n,
                                b.merge(this, b.parseHTML(i[1], n && n.nodeType ? n.ownerDocument || n : s, !0)),
                            T.test(i[1]) && b.isPlainObject(n))
                            for (i in n)
                                b.isFunction(this[i]) ? this[i](n[i]) : this.attr(i, n[i]);
                        return this
                    }
                    if (o = s.getElementById(i[2]),
                        o && o.parentNode) {
                        if (o.id !== i[2])
                            return r.find(e);
                        this.length = 1,
                            this[0] = o
                    }
                    return this.context = s,
                        this.selector = e,
                        this
                }
                return e.nodeType ? (this.context = this[0] = e,
                    this.length = 1,
                    this) : b.isFunction(e) ? r.ready(e) : (e.selector !== t && (this.selector = e.selector,
                    this.context = e.context),
                    b.makeArray(e, this))
            },
            selector: "",
            length: 0,
            size: function() {
                return this.length
            },
            toArray: function() {
                return d.call(this)
            },
            get: function(e) {
                return null == e ? this.toArray() : 0 > e ? this[this.length + e] : this[e]
            },
            pushStack: function(e) {
                var t = b.merge(this.constructor(), e);
                return t.prevObject = this,
                    t.context = this.context,
                    t
            },
            each: function(e, t) {
                return b.each(this, e, t)
            },
            ready: function(e) {
                return b.ready.promise().done(e),
                    this
            },
            slice: function() {
                return this.pushStack(d.apply(this, arguments))
            },
            first: function() {
                return this.eq(0)
            },
            last: function() {
                return this.eq(-1)
            },
            eq: function(e) {
                var t = this.length
                    , n = +e + (0 > e ? t : 0);
                return this.pushStack(n >= 0 && t > n ? [this[n]] : [])
            },
            map: function(e) {
                return this.pushStack(b.map(this, function(t, n) {
                    return e.call(t, n, t)
                }))
            },
            end: function() {
                return this.prevObject || this.constructor(null)
            },
            push: p,
            sort: [].sort,
            splice: [].splice
        },
            b.fn.init.prototype = b.fn,
            b.extend = b.fn.extend = function() {
                var e, n, r, i, s, o, u = arguments[0] || {}, a = 1, f = arguments.length, l = !1;
                for ("boolean" == typeof u && (l = u,
                    u = arguments[1] || {},
                    a = 2),
                     "object" == typeof u || b.isFunction(u) || (u = {}),
                     f === a && (u = this,
                         --a); f > a; a++)
                    if (null != (s = arguments[a]))
                        for (i in s)
                            e = u[i],
                                r = s[i],
                            u !== r && (l && r && (b.isPlainObject(r) || (n = b.isArray(r))) ? (n ? (n = !1,
                                o = e && b.isArray(e) ? e : []) : o = e && b.isPlainObject(e) ? e : {},
                                u[i] = b.extend(l, o, r)) : r !== t && (u[i] = r));
                return u
            }
            ,
            b.extend({
                noConflict: function(t) {
                    return e.$ === b && (e.$ = a),
                    t && e.jQuery === b && (e.jQuery = u),
                        b
                },
                isReady: !1,
                readyWait: 1,
                holdReady: function(e) {
                    e ? b.readyWait++ : b.ready(!0)
                },
                ready: function(e) {
                    if (e === !0 ? !--b.readyWait : !b.isReady) {
                        if (!s.body)
                            return setTimeout(b.ready);
                        b.isReady = !0,
                        e !== !0 && --b.readyWait > 0 || (n.resolveWith(s, [b]),
                        b.fn.trigger && b(s).trigger("ready").off("ready"))
                    }
                },
                isFunction: function(e) {
                    return "function" === b.type(e)
                },
                isArray: Array.isArray || function(e) {
                    return "array" === b.type(e)
                }
                ,
                isWindow: function(e) {
                    return null != e && e == e.window
                },
                isNumeric: function(e) {
                    return !isNaN(parseFloat(e)) && isFinite(e)
                },
                type: function(e) {
                    return null == e ? e + "" : "object" == typeof e || "function" == typeof e ? f[m.call(e)] || "object" : typeof e
                },
                isPlainObject: function(e) {
                    if (!e || "object" !== b.type(e) || e.nodeType || b.isWindow(e))
                        return !1;
                    try {
                        if (e.constructor && !g.call(e, "constructor") && !g.call(e.constructor.prototype, "isPrototypeOf"))
                            return !1
                    } catch (n) {
                        return !1
                    }
                    var r;
                    for (r in e)
                        ;
                    return r === t || g.call(e, r)
                },
                isEmptyObject: function(e) {
                    var t;
                    for (t in e)
                        return !1;
                    return !0
                },
                error: function(e) {
                    throw Error(e)
                },
                parseHTML: function(e, t, n) {
                    if (!e || "string" != typeof e)
                        return null;
                    "boolean" == typeof t && (n = t,
                        t = !1),
                        t = t || s;
                    var r = T.exec(e)
                        , i = !n && [];
                    return r ? [t.createElement(r[1])] : (r = b.buildFragment([e], t, i),
                    i && b(i).remove(),
                        b.merge([], r.childNodes))
                },
                parseJSON: function(n) {
                    return e.JSON && e.JSON.parse ? e.JSON.parse(n) : null === n ? n : "string" == typeof n && (n = b.trim(n),
                    n && N.test(n.replace(k, "@").replace(L, "]").replace(C, ""))) ? Function("return " + n)() : (b.error("Invalid JSON: " + n),
                        t)
                },
                parseXML: function(n) {
                    var r, i;
                    if (!n || "string" != typeof n)
                        return null;
                    try {
                        e.DOMParser ? (i = new DOMParser,
                            r = i.parseFromString(n, "text/xml")) : (r = new ActiveXObject("Microsoft.XMLDOM"),
                            r.async = "false",
                            r.loadXML(n))
                    } catch (s) {
                        r = t
                    }
                    return r && r.documentElement && !r.getElementsByTagName("parsererror").length || b.error("Invalid XML: " + n),
                        r
                },
                noop: function() {},
                globalEval: function(t) {
                    t && b.trim(t) && (e.execScript || function(t) {
                            e.eval.call(e, t)
                        }
                    )(t)
                },
                camelCase: function(e) {
                    return e.replace(A, "ms-").replace(O, M)
                },
                nodeName: function(e, t) {
                    return e.nodeName && e.nodeName.toLowerCase() === t.toLowerCase()
                },
                each: function(e, t, n) {
                    var r, i = 0, s = e.length, o = P(e);
                    if (n) {
                        if (o) {
                            for (; s > i; i++)
                                if (r = t.apply(e[i], n),
                                    r === !1)
                                    break
                        } else
                            for (i in e)
                                if (r = t.apply(e[i], n),
                                    r === !1)
                                    break
                    } else if (o) {
                        for (; s > i; i++)
                            if (r = t.call(e[i], i, e[i]),
                                r === !1)
                                break
                    } else
                        for (i in e)
                            if (r = t.call(e[i], i, e[i]),
                                r === !1)
                                break;
                    return e
                },
                trim: y && !y.call("﻿ ") ? function(e) {
                    return null == e ? "" : y.call(e)
                }
                    : function(e) {
                    return null == e ? "" : (e + "").replace(S, "")
                }
                ,
                makeArray: function(e, t) {
                    var n = t || [];
                    return null != e && (P(Object(e)) ? b.merge(n, "string" == typeof e ? [e] : e) : p.call(n, e)),
                        n
                },
                inArray: function(e, t, n) {
                    var r;
                    if (t) {
                        if (v)
                            return v.call(t, e, n);
                        for (r = t.length,
                                 n = n ? 0 > n ? Math.max(0, r + n) : n : 0; r > n; n++)
                            if (n in t && t[n] === e)
                                return n
                    }
                    return -1
                },
                merge: function(e, n) {
                    var r = n.length
                        , i = e.length
                        , s = 0;
                    if ("number" == typeof r)
                        for (; r > s; s++)
                            e[i++] = n[s];
                    else
                        while (n[s] !== t)
                            e[i++] = n[s++];
                    return e.length = i,
                        e
                },
                grep: function(e, t, n) {
                    var r, i = [], s = 0, o = e.length;
                    for (n = !!n; o > s; s++)
                        r = !!t(e[s], s),
                        n !== r && i.push(e[s]);
                    return i
                },
                map: function(e, t, n) {
                    var r, i = 0, s = e.length, o = P(e), u = [];
                    if (o)
                        for (; s > i; i++)
                            r = t(e[i], i, n),
                            null != r && (u[u.length] = r);
                    else
                        for (i in e)
                            r = t(e[i], i, n),
                            null != r && (u[u.length] = r);
                    return h.apply([], u)
                },
                guid: 1,
                proxy: function(e, n) {
                    var r, i, s;
                    return "string" == typeof n && (s = e[n],
                        n = e,
                        e = s),
                        b.isFunction(e) ? (r = d.call(arguments, 2),
                            i = function() {
                                return e.apply(n || this, r.concat(d.call(arguments)))
                            }
                            ,
                            i.guid = e.guid = e.guid || b.guid++,
                            i) : t
                },
                access: function(e, n, r, i, s, o, u) {
                    var a = 0
                        , f = e.length
                        , l = null == r;
                    if ("object" === b.type(r)) {
                        s = !0;
                        for (a in r)
                            b.access(e, n, a, r[a], !0, o, u)
                    } else if (i !== t && (s = !0,
                        b.isFunction(i) || (u = !0),
                        l && (u ? (n.call(e, i),
                            n = null) : (l = n,
                                n = function(e, t, n) {
                                    return l.call(b(e), n)
                                }
                        )),
                            n))
                        for (; f > a; a++)
                            n(e[a], r, u ? i : i.call(e[a], a, n(e[a], r)));
                    return s ? e : l ? n.call(e) : f ? n(e[0], r) : o
                },
                now: function() {
                    return (new Date).getTime()
                }
            }),
            b.ready.promise = function(t) {
                if (!n)
                    if (n = b.Deferred(),
                        "complete" === s.readyState)
                        setTimeout(b.ready);
                    else if (s.addEventListener)
                        s.addEventListener("DOMContentLoaded", _, !1),
                            e.addEventListener("load", _, !1);
                    else {
                        s.attachEvent("onreadystatechange", _),
                            e.attachEvent("onload", _);
                        var r = !1;
                        try {
                            r = null == e.frameElement && s.documentElement
                        } catch (i) {}
                        r && r.doScroll && function o() {
                            if (!b.isReady) {
                                try {
                                    r.doScroll("left")
                                } catch (e) {
                                    return setTimeout(o, 50)
                                }
                                D(),
                                    b.ready()
                            }
                        }()
                    }
                return n.promise(t)
            }
            ,
            b.each("Boolean Number String Function Array Date RegExp Object Error".split(" "), function(e, t) {
                f["[object " + t + "]"] = t.toLowerCase()
            }),
            r = b(s);
        var H = {};
        b.Callbacks = function(e) {
            e = "string" == typeof e ? H[e] || B(e) : b.extend({}, e);
            var n, r, i, s, o, u, a = [], f = !e.once && [], l = function(t) {
                for (r = e.memory && t,
                         i = !0,
                         o = u || 0,
                         u = 0,
                         s = a.length,
                         n = !0; a && s > o; o++)
                    if (a[o].apply(t[0], t[1]) === !1 && e.stopOnFalse) {
                        r = !1;
                        break
                    }
                n = !1,
                a && (f ? f.length && l(f.shift()) : r ? a = [] : c.disable())
            }, c = {
                add: function() {
                    if (a) {
                        var t = a.length;
                        (function i(t) {
                            b.each(t, function(t, n) {
                                var r = b.type(n);
                                "function" === r ? e.unique && c.has(n) || a.push(n) : n && n.length && "string" !== r && i(n)
                            })
                        })(arguments),
                            n ? s = a.length : r && (u = t,
                                l(r))
                    }
                    return this
                },
                remove: function() {
                    return a && b.each(arguments, function(e, t) {
                        var r;
                        while ((r = b.inArray(t, a, r)) > -1)
                            a.splice(r, 1),
                            n && (s >= r && s--,
                            o >= r && o--)
                    }),
                        this
                },
                has: function(e) {
                    return e ? b.inArray(e, a) > -1 : !!a && !!a.length
                },
                empty: function() {
                    return a = [],
                        this
                },
                disable: function() {
                    return a = f = r = t,
                        this
                },
                disabled: function() {
                    return !a
                },
                lock: function() {
                    return f = t,
                    r || c.disable(),
                        this
                },
                locked: function() {
                    return !f
                },
                fireWith: function(e, t) {
                    return t = t || [],
                        t = [e, t.slice ? t.slice() : t],
                    !a || i && !f || (n ? f.push(t) : l(t)),
                        this
                },
                fire: function() {
                    return c.fireWith(this, arguments),
                        this
                },
                fired: function() {
                    return !!i
                }
            };
            return c
        }
            ,
            b.extend({
                Deferred: function(e) {
                    var t = [["resolve", "done", b.Callbacks("once memory"), "resolved"], ["reject", "fail", b.Callbacks("once memory"), "rejected"], ["notify", "progress", b.Callbacks("memory")]]
                        , n = "pending"
                        , r = {
                        state: function() {
                            return n
                        },
                        always: function() {
                            return i.done(arguments).fail(arguments),
                                this
                        },
                        then: function() {
                            var e = arguments;
                            return b.Deferred(function(n) {
                                b.each(t, function(t, s) {
                                    var o = s[0]
                                        , u = b.isFunction(e[t]) && e[t];
                                    i[s[1]](function() {
                                        var e = u && u.apply(this, arguments);
                                        e && b.isFunction(e.promise) ? e.promise().done(n.resolve).fail(n.reject).progress(n.notify) : n[o + "With"](this === r ? n.promise() : this, u ? [e] : arguments)
                                    })
                                }),
                                    e = null
                            }).promise()
                        },
                        promise: function(e) {
                            return null != e ? b.extend(e, r) : r
                        }
                    }
                        , i = {};
                    return r.pipe = r.then,
                        b.each(t, function(e, s) {
                            var o = s[2]
                                , u = s[3];
                            r[s[1]] = o.add,
                            u && o.add(function() {
                                n = u
                            }, t[1 ^ e][2].disable, t[2][2].lock),
                                i[s[0]] = function() {
                                    return i[s[0] + "With"](this === i ? r : this, arguments),
                                        this
                                }
                                ,
                                i[s[0] + "With"] = o.fireWith
                        }),
                        r.promise(i),
                    e && e.call(i, i),
                        i
                },
                when: function(e) {
                    var t = 0, n = d.call(arguments), r = n.length, i = 1 !== r || e && b.isFunction(e.promise) ? r : 0, s = 1 === i ? e : b.Deferred(), o = function(e, t, n) {
                        return function(r) {
                            t[e] = this,
                                n[e] = arguments.length > 1 ? d.call(arguments) : r,
                                n === u ? s.notifyWith(t, n) : --i || s.resolveWith(t, n)
                        }
                    }, u, a, f;
                    if (r > 1)
                        for (u = Array(r),
                                 a = Array(r),
                                 f = Array(r); r > t; t++)
                            n[t] && b.isFunction(n[t].promise) ? n[t].promise().done(o(t, f, n)).fail(s.reject).progress(o(t, a, u)) : --i;
                    return i || s.resolveWith(f, n),
                        s.promise()
                }
            }),
            b.support = function() {
                var t, n, r, o, u, a, f, l, c, h, p = s.createElement("div");
                if (p.setAttribute("className", "t"),
                        p.innerHTML = "  <link/><table></table><a href='/a'>a</a><input type='checkbox'/>",
                        n = p.getElementsByTagName("*"),
                        r = p.getElementsByTagName("a")[0],
                    !n || !r || !n.length)
                    return {};
                u = s.createElement("select"),
                    f = u.appendChild(s.createElement("option")),
                    o = p.getElementsByTagName("input")[0],
                    r.style.cssText = "top:1px;float:left;opacity:.5",
                    t = {
                        getSetAttribute: "t" !== p.className,
                        leadingWhitespace: 3 === p.firstChild.nodeType,
                        tbody: !p.getElementsByTagName("tbody").length,
                        htmlSerialize: !!p.getElementsByTagName("link").length,
                        style: /top/.test(r.getAttribute("style")),
                        hrefNormalized: "/a" === r.getAttribute("href"),
                        opacity: /^0.5/.test(r.style.opacity),
                        cssFloat: !!r.style.cssFloat,
                        checkOn: !!o.value,
                        optSelected: f.selected,
                        enctype: !!s.createElement("form").enctype,
                        html5Clone: "<:nav></:nav>" !== s.createElement("nav").cloneNode(!0).outerHTML,
                        boxModel: "CSS1Compat" === s.compatMode,
                        deleteExpando: !0,
                        noCloneEvent: !0,
                        inlineBlockNeedsLayout: !1,
                        shrinkWrapBlocks: !1,
                        reliableMarginRight: !0,
                        boxSizingReliable: !0,
                        pixelPosition: !1
                    },
                    o.checked = !0,
                    t.noCloneChecked = o.cloneNode(!0).checked,
                    u.disabled = !0,
                    t.optDisabled = !f.disabled;
                try {
                    delete p.test
                } catch (d) {
                    t.deleteExpando = !1
                }
                o = s.createElement("input"),
                    o.setAttribute("value", ""),
                    t.input = "" === o.getAttribute("value"),
                    o.value = "t",
                    o.setAttribute("type", "radio"),
                    t.radioValue = "t" === o.value,
                    o.setAttribute("checked", "t"),
                    o.setAttribute("name", "t"),
                    a = s.createDocumentFragment(),
                    a.appendChild(o),
                    t.appendChecked = o.checked,
                    t.checkClone = a.cloneNode(!0).cloneNode(!0).lastChild.checked,
                p.attachEvent && (p.attachEvent("onclick", function() {
                    t.noCloneEvent = !1
                }),
                    p.cloneNode(!0).click());
                for (h in {
                    submit: !0,
                    change: !0,
                    focusin: !0
                })
                    p.setAttribute(l = "on" + h, "t"),
                        t[h + "Bubbles"] = l in e || p.attributes[l].expando === !1;
                return p.style.backgroundClip = "content-box",
                    p.cloneNode(!0).style.backgroundClip = "",
                    t.clearCloneStyle = "content-box" === p.style.backgroundClip,
                    b(function() {
                        var n, r, o, u = "padding:0;margin:0;border:0;display:block;box-sizing:content-box;-moz-box-sizing:content-box;-webkit-box-sizing:content-box;", a = s.getElementsByTagName("body")[0];
                        a && (n = s.createElement("div"),
                            n.style.cssText = "border:0;width:0;height:0;position:absolute;top:0;left:-9999px;margin-top:1px",
                            a.appendChild(n).appendChild(p),
                            p.innerHTML = "<table><tr><td></td><td>t</td></tr></table>",
                            o = p.getElementsByTagName("td"),
                            o[0].style.cssText = "padding:0;margin:0;border:0;display:none",
                            c = 0 === o[0].offsetHeight,
                            o[0].style.display = "",
                            o[1].style.display = "none",
                            t.reliableHiddenOffsets = c && 0 === o[0].offsetHeight,
                            p.innerHTML = "",
                            p.style.cssText = "box-sizing:border-box;-moz-box-sizing:border-box;-webkit-box-sizing:border-box;padding:1px;border:1px;display:block;width:4px;margin-top:1%;position:absolute;top:1%;",
                            t.boxSizing = 4 === p.offsetWidth,
                            t.doesNotIncludeMarginInBodyOffset = 1 !== a.offsetTop,
                        e.getComputedStyle && (t.pixelPosition = "1%" !== (e.getComputedStyle(p, null) || {}).top,
                            t.boxSizingReliable = "4px" === (e.getComputedStyle(p, null) || {
                                    width: "4px"
                                }).width,
                            r = p.appendChild(s.createElement("div")),
                            r.style.cssText = p.style.cssText = u,
                            r.style.marginRight = r.style.width = "0",
                            p.style.width = "1px",
                            t.reliableMarginRight = !parseFloat((e.getComputedStyle(r, null) || {}).marginRight)),
                        typeof p.style.zoom !== i && (p.innerHTML = "",
                            p.style.cssText = u + "width:1px;padding:1px;display:inline;zoom:1",
                            t.inlineBlockNeedsLayout = 3 === p.offsetWidth,
                            p.style.display = "block",
                            p.innerHTML = "<div></div>",
                            p.firstChild.style.width = "5px",
                            t.shrinkWrapBlocks = 3 !== p.offsetWidth,
                        t.inlineBlockNeedsLayout && (a.style.zoom = 1)),
                            a.removeChild(n),
                            n = p = o = r = null)
                    }),
                    n = u = a = f = r = o = null,
                    t
            }();
        var j = /(?:\{[\s\S]*\}|\[[\s\S]*\])$/
            , F = /([A-Z])/g;
        b.extend({
            cache: {},
            expando: "jQuery" + (c + Math.random()).replace(/\D/g, ""),
            noData: {
                embed: !0,
                object: "clsid:D27CDB6E-AE6D-11cf-96B8-444553540000",
                applet: !0
            },
            hasData: function(e) {
                return e = e.nodeType ? b.cache[e[b.expando]] : e[b.expando],
                !!e && !U(e)
            },
            data: function(e, t, n) {
                return I(e, t, n)
            },
            removeData: function(e, t) {
                return q(e, t)
            },
            _data: function(e, t, n) {
                return I(e, t, n, !0)
            },
            _removeData: function(e, t) {
                return q(e, t, !0)
            },
            acceptData: function(e) {
                if (e.nodeType && 1 !== e.nodeType && 9 !== e.nodeType)
                    return !1;
                var t = e.nodeName && b.noData[e.nodeName.toLowerCase()];
                return !t || t !== !0 && e.getAttribute("classid") === t
            }
        }),
            b.fn.extend({
                data: function(e, n) {
                    var r, i, s = this[0], o = 0, u = null;
                    if (e === t) {
                        if (this.length && (u = b.data(s),
                            1 === s.nodeType && !b._data(s, "parsedAttrs"))) {
                            for (r = s.attributes; r.length > o; o++)
                                i = r[o].name,
                                i.indexOf("data-") || (i = b.camelCase(i.slice(5)),
                                    R(s, i, u[i]));
                            b._data(s, "parsedAttrs", !0)
                        }
                        return u
                    }
                    return "object" == typeof e ? this.each(function() {
                        b.data(this, e)
                    }) : b.access(this, function(n) {
                        return n === t ? s ? R(s, e, b.data(s, e)) : null : (this.each(function() {
                            b.data(this, e, n)
                        }),
                            t)
                    }, null, n, arguments.length > 1, null, !0)
                },
                removeData: function(e) {
                    return this.each(function() {
                        b.removeData(this, e)
                    })
                }
            }),
            b.extend({
                queue: function(e, n, r) {
                    var i;
                    return e ? (n = (n || "fx") + "queue",
                        i = b._data(e, n),
                    r && (!i || b.isArray(r) ? i = b._data(e, n, b.makeArray(r)) : i.push(r)),
                    i || []) : t
                },
                dequeue: function(e, t) {
                    t = t || "fx";
                    var n = b.queue(e, t)
                        , r = n.length
                        , i = n.shift()
                        , s = b._queueHooks(e, t)
                        , o = function() {
                        b.dequeue(e, t)
                    };
                    "inprogress" === i && (i = n.shift(),
                        r--),
                        s.cur = i,
                    i && ("fx" === t && n.unshift("inprogress"),
                        delete s.stop,
                        i.call(e, o, s)),
                    !r && s && s.empty.fire()
                },
                _queueHooks: function(e, t) {
                    var n = t + "queueHooks";
                    return b._data(e, n) || b._data(e, n, {
                            empty: b.Callbacks("once memory").add(function() {
                                b._removeData(e, t + "queue"),
                                    b._removeData(e, n)
                            })
                        })
                }
            }),
            b.fn.extend({
                queue: function(e, n) {
                    var r = 2;
                    return "string" != typeof e && (n = e,
                        e = "fx",
                        r--),
                        r > arguments.length ? b.queue(this[0], e) : n === t ? this : this.each(function() {
                            var t = b.queue(this, e, n);
                            b._queueHooks(this, e),
                            "fx" === e && "inprogress" !== t[0] && b.dequeue(this, e)
                        })
                },
                dequeue: function(e) {
                    return this.each(function() {
                        b.dequeue(this, e)
                    })
                },
                delay: function(e, t) {
                    return e = b.fx ? b.fx.speeds[e] || e : e,
                        t = t || "fx",
                        this.queue(t, function(t, n) {
                            var r = setTimeout(t, e);
                            n.stop = function() {
                                clearTimeout(r)
                            }
                        })
                },
                clearQueue: function(e) {
                    return this.queue(e || "fx", [])
                },
                promise: function(e, n) {
                    var r, i = 1, s = b.Deferred(), o = this, u = this.length, a = function() {
                        --i || s.resolveWith(o, [o])
                    };
                    "string" != typeof e && (n = e,
                        e = t),
                        e = e || "fx";
                    while (u--)
                        r = b._data(o[u], e + "queueHooks"),
                        r && r.empty && (i++,
                            r.empty.add(a));
                    return a(),
                        s.promise(n)
                }
            });
        var z, W, X = /[\t\r\n]/g, V = /\r/g, $ = /^(?:input|select|textarea|button|object)$/i, J = /^(?:a|area)$/i, K = /^(?:checked|selected|autofocus|autoplay|async|controls|defer|disabled|hidden|loop|multiple|open|readonly|required|scoped)$/i, Q = /^(?:checked|selected)$/i, G = b.support.getSetAttribute, Y = b.support.input;
        b.fn.extend({
            attr: function(e, t) {
                return b.access(this, b.attr, e, t, arguments.length > 1)
            },
            removeAttr: function(e) {
                return this.each(function() {
                    b.removeAttr(this, e)
                })
            },
            prop: function(e, t) {
                return b.access(this, b.prop, e, t, arguments.length > 1)
            },
            removeProp: function(e) {
                return e = b.propFix[e] || e,
                    this.each(function() {
                        try {
                            this[e] = t,
                                delete this[e]
                        } catch (n) {}
                    })
            },
            addClass: function(e) {
                var t, n, r, i, s, o = 0, u = this.length, a = "string" == typeof e && e;
                if (b.isFunction(e))
                    return this.each(function(t) {
                        b(this).addClass(e.call(this, t, this.className))
                    });
                if (a)
                    for (t = (e || "").match(E) || []; u > o; o++)
                        if (n = this[o],
                                r = 1 === n.nodeType && (n.className ? (" " + n.className + " ").replace(X, " ") : " ")) {
                            s = 0;
                            while (i = t[s++])
                                0 > r.indexOf(" " + i + " ") && (r += i + " ");
                            n.className = b.trim(r)
                        }
                return this
            },
            removeClass: function(e) {
                var t, n, r, i, s, o = 0, u = this.length, a = 0 === arguments.length || "string" == typeof e && e;
                if (b.isFunction(e))
                    return this.each(function(t) {
                        b(this).removeClass(e.call(this, t, this.className))
                    });
                if (a)
                    for (t = (e || "").match(E) || []; u > o; o++)
                        if (n = this[o],
                                r = 1 === n.nodeType && (n.className ? (" " + n.className + " ").replace(X, " ") : "")) {
                            s = 0;
                            while (i = t[s++])
                                while (r.indexOf(" " + i + " ") >= 0)
                                    r = r.replace(" " + i + " ", " ");
                            n.className = e ? b.trim(r) : ""
                        }
                return this
            },
            toggleClass: function(e, t) {
                var n = typeof e
                    , r = "boolean" == typeof t;
                return b.isFunction(e) ? this.each(function(n) {
                    b(this).toggleClass(e.call(this, n, this.className, t), t)
                }) : this.each(function() {
                    if ("string" === n) {
                        var s, o = 0, u = b(this), a = t, f = e.match(E) || [];
                        while (s = f[o++])
                            a = r ? a : !u.hasClass(s),
                                u[a ? "addClass" : "removeClass"](s)
                    } else
                        (n === i || "boolean" === n) && (this.className && b._data(this, "__className__", this.className),
                            this.className = this.className || e === !1 ? "" : b._data(this, "__className__") || "")
                })
            },
            hasClass: function(e) {
                var t = " " + e + " "
                    , n = 0
                    , r = this.length;
                for (; r > n; n++)
                    if (1 === this[n].nodeType && (" " + this[n].className + " ").replace(X, " ").indexOf(t) >= 0)
                        return !0;
                return !1
            },
            val: function(e) {
                var n, r, i, s = this[0];
                if (arguments.length)
                    return i = b.isFunction(e),
                        this.each(function(n) {
                            var s, o = b(this);
                            1 === this.nodeType && (s = i ? e.call(this, n, o.val()) : e,
                                null == s ? s = "" : "number" == typeof s ? s += "" : b.isArray(s) && (s = b.map(s, function(e) {
                                    return null == e ? "" : e + ""
                                })),
                                r = b.valHooks[this.type] || b.valHooks[this.nodeName.toLowerCase()],
                            r && "set"in r && r.set(this, s, "value") !== t || (this.value = s))
                        });
                if (s)
                    return r = b.valHooks[s.type] || b.valHooks[s.nodeName.toLowerCase()],
                        r && "get"in r && (n = r.get(s, "value")) !== t ? n : (n = s.value,
                            "string" == typeof n ? n.replace(V, "") : null == n ? "" : n)
            }
        }),
            b.extend({
                valHooks: {
                    option: {
                        get: function(e) {
                            var t = e.attributes.value;
                            return !t || t.specified ? e.value : e.text
                        }
                    },
                    select: {
                        get: function(e) {
                            var t, n, r = e.options, i = e.selectedIndex, s = "select-one" === e.type || 0 > i, o = s ? null : [], u = s ? i + 1 : r.length, a = 0 > i ? u : s ? i : 0;
                            for (; u > a; a++)
                                if (n = r[a],
                                        !(!n.selected && a !== i || (b.support.optDisabled ? n.disabled : null !== n.getAttribute("disabled")) || n.parentNode.disabled && b.nodeName(n.parentNode, "optgroup"))) {
                                    if (t = b(n).val(),
                                            s)
                                        return t;
                                    o.push(t)
                                }
                            return o
                        },
                        set: function(e, t) {
                            var n = b.makeArray(t);
                            return b(e).find("option").each(function() {
                                this.selected = b.inArray(b(this).val(), n) >= 0
                            }),
                            n.length || (e.selectedIndex = -1),
                                n
                        }
                    }
                },
                attr: function(e, n, r) {
                    var s, o, u, a = e.nodeType;
                    if (e && 3 !== a && 8 !== a && 2 !== a)
                        return typeof e.getAttribute === i ? b.prop(e, n, r) : (o = 1 !== a || !b.isXMLDoc(e),
                        o && (n = n.toLowerCase(),
                            s = b.attrHooks[n] || (K.test(n) ? W : z)),
                            r === t ? s && o && "get"in s && null !== (u = s.get(e, n)) ? u : (typeof e.getAttribute !== i && (u = e.getAttribute(n)),
                                null == u ? t : u) : null !== r ? s && o && "set"in s && (u = s.set(e, r, n)) !== t ? u : (e.setAttribute(n, r + ""),
                                r) : (b.removeAttr(e, n),
                                t))
                },
                removeAttr: function(e, t) {
                    var n, r, i = 0, s = t && t.match(E);
                    if (s && 1 === e.nodeType)
                        while (n = s[i++])
                            r = b.propFix[n] || n,
                                K.test(n) ? !G && Q.test(n) ? e[b.camelCase("default-" + n)] = e[r] = !1 : e[r] = !1 : b.attr(e, n, ""),
                                e.removeAttribute(G ? n : r)
                },
                attrHooks: {
                    type: {
                        set: function(e, t) {
                            if (!b.support.radioValue && "radio" === t && b.nodeName(e, "input")) {
                                var n = e.value;
                                return e.setAttribute("type", t),
                                n && (e.value = n),
                                    t
                            }
                        }
                    }
                },
                propFix: {
                    tabindex: "tabIndex",
                    readonly: "readOnly",
                    "for": "htmlFor",
                    "class": "className",
                    maxlength: "maxLength",
                    cellspacing: "cellSpacing",
                    cellpadding: "cellPadding",
                    rowspan: "rowSpan",
                    colspan: "colSpan",
                    usemap: "useMap",
                    frameborder: "frameBorder",
                    contenteditable: "contentEditable"
                },
                prop: function(e, n, r) {
                    var i, s, o, u = e.nodeType;
                    if (e && 3 !== u && 8 !== u && 2 !== u)
                        return o = 1 !== u || !b.isXMLDoc(e),
                        o && (n = b.propFix[n] || n,
                            s = b.propHooks[n]),
                            r !== t ? s && "set"in s && (i = s.set(e, r, n)) !== t ? i : e[n] = r : s && "get"in s && null !== (i = s.get(e, n)) ? i : e[n]
                },
                propHooks: {
                    tabIndex: {
                        get: function(e) {
                            var n = e.getAttributeNode("tabindex");
                            return n && n.specified ? parseInt(n.value, 10) : $.test(e.nodeName) || J.test(e.nodeName) && e.href ? 0 : t
                        }
                    }
                }
            }),
            W = {
                get: function(e, n) {
                    var r = b.prop(e, n)
                        , i = "boolean" == typeof r && e.getAttribute(n)
                        , s = "boolean" == typeof r ? Y && G ? null != i : Q.test(n) ? e[b.camelCase("default-" + n)] : !!i : e.getAttributeNode(n);
                    return s && s.value !== !1 ? n.toLowerCase() : t
                },
                set: function(e, t, n) {
                    return t === !1 ? b.removeAttr(e, n) : Y && G || !Q.test(n) ? e.setAttribute(!G && b.propFix[n] || n, n) : e[b.camelCase("default-" + n)] = e[n] = !0,
                        n
                }
            },
        Y && G || (b.attrHooks.value = {
            get: function(e, n) {
                var r = e.getAttributeNode(n);
                return b.nodeName(e, "input") ? e.defaultValue : r && r.specified ? r.value : t
            },
            set: function(e, n, r) {
                return b.nodeName(e, "input") ? (e.defaultValue = n,
                    t) : z && z.set(e, n, r)
            }
        }),
        G || (z = b.valHooks.button = {
            get: function(e, n) {
                var r = e.getAttributeNode(n);
                return r && ("id" === n || "name" === n || "coords" === n ? "" !== r.value : r.specified) ? r.value : t
            },
            set: function(e, n, r) {
                var i = e.getAttributeNode(r);
                return i || e.setAttributeNode(i = e.ownerDocument.createAttribute(r)),
                    i.value = n += "",
                    "value" === r || n === e.getAttribute(r) ? n : t
            }
        },
            b.attrHooks.contenteditable = {
                get: z.get,
                set: function(e, t, n) {
                    z.set(e, "" === t ? !1 : t, n)
                }
            },
            b.each(["width", "height"], function(e, n) {
                b.attrHooks[n] = b.extend(b.attrHooks[n], {
                    set: function(e, r) {
                        return "" === r ? (e.setAttribute(n, "auto"),
                            r) : t
                    }
                })
            })),
        b.support.hrefNormalized || (b.each(["href", "src", "width", "height"], function(e, n) {
            b.attrHooks[n] = b.extend(b.attrHooks[n], {
                get: function(e) {
                    var r = e.getAttribute(n, 2);
                    return null == r ? t : r
                }
            })
        }),
            b.each(["href", "src"], function(e, t) {
                b.propHooks[t] = {
                    get: function(e) {
                        return e.getAttribute(t, 4)
                    }
                }
            })),
        b.support.style || (b.attrHooks.style = {
            get: function(e) {
                return e.style.cssText || t
            },
            set: function(e, t) {
                return e.style.cssText = t + ""
            }
        }),
        b.support.optSelected || (b.propHooks.selected = b.extend(b.propHooks.selected, {
            get: function(e) {
                var t = e.parentNode;
                return t && (t.selectedIndex,
                t.parentNode && t.parentNode.selectedIndex),
                    null
            }
        })),
        b.support.enctype || (b.propFix.enctype = "encoding"),
        b.support.checkOn || b.each(["radio", "checkbox"], function() {
            b.valHooks[this] = {
                get: function(e) {
                    return null === e.getAttribute("value") ? "on" : e.value
                }
            }
        }),
            b.each(["radio", "checkbox"], function() {
                b.valHooks[this] = b.extend(b.valHooks[this], {
                    set: function(e, n) {
                        return b.isArray(n) ? e.checked = b.inArray(b(e).val(), n) >= 0 : t
                    }
                })
            });
        var Z = /^(?:input|select|textarea)$/i
            , et = /^key/
            , tt = /^(?:mouse|contextmenu)|click/
            , nt = /^(?:focusinfocus|focusoutblur)$/
            , rt = /^([^.]*)(?:\.(.+)|)$/;
        b.event = {
            global: {},
            add: function(e, n, r, s, o) {
                var u, a, f, l, c, h, p, d, v, m, g, y = b._data(e);
                if (y) {
                    r.handler && (l = r,
                        r = l.handler,
                        o = l.selector),
                    r.guid || (r.guid = b.guid++),
                    (a = y.events) || (a = y.events = {}),
                    (h = y.handle) || (h = y.handle = function(e) {
                        return typeof b === i || e && b.event.triggered === e.type ? t : b.event.dispatch.apply(h.elem, arguments)
                    }
                        ,
                        h.elem = e),
                        n = (n || "").match(E) || [""],
                        f = n.length;
                    while (f--)
                        u = rt.exec(n[f]) || [],
                            v = g = u[1],
                            m = (u[2] || "").split(".").sort(),
                            c = b.event.special[v] || {},
                            v = (o ? c.delegateType : c.bindType) || v,
                            c = b.event.special[v] || {},
                            p = b.extend({
                                type: v,
                                origType: g,
                                data: s,
                                handler: r,
                                guid: r.guid,
                                selector: o,
                                needsContext: o && b.expr.match.needsContext.test(o),
                                namespace: m.join(".")
                            }, l),
                        (d = a[v]) || (d = a[v] = [],
                            d.delegateCount = 0,
                        c.setup && c.setup.call(e, s, m, h) !== !1 || (e.addEventListener ? e.addEventListener(v, h, !1) : e.attachEvent && e.attachEvent("on" + v, h))),
                        c.add && (c.add.call(e, p),
                        p.handler.guid || (p.handler.guid = r.guid)),
                            o ? d.splice(d.delegateCount++, 0, p) : d.push(p),
                            b.event.global[v] = !0;
                    e = null
                }
            },
            remove: function(e, t, n, r, i) {
                var s, o, u, a, f, l, c, h, p, d, v, m = b.hasData(e) && b._data(e);
                if (m && (l = m.events)) {
                    t = (t || "").match(E) || [""],
                        f = t.length;
                    while (f--)
                        if (u = rt.exec(t[f]) || [],
                                p = v = u[1],
                                d = (u[2] || "").split(".").sort(),
                                p) {
                            c = b.event.special[p] || {},
                                p = (r ? c.delegateType : c.bindType) || p,
                                h = l[p] || [],
                                u = u[2] && RegExp("(^|\\.)" + d.join("\\.(?:.*\\.|)") + "(\\.|$)"),
                                a = s = h.length;
                            while (s--)
                                o = h[s],
                                !i && v !== o.origType || n && n.guid !== o.guid || u && !u.test(o.namespace) || r && r !== o.selector && ("**" !== r || !o.selector) || (h.splice(s, 1),
                                o.selector && h.delegateCount--,
                                c.remove && c.remove.call(e, o));
                            a && !h.length && (c.teardown && c.teardown.call(e, d, m.handle) !== !1 || b.removeEvent(e, p, m.handle),
                                delete l[p])
                        } else
                            for (p in l)
                                b.event.remove(e, p + t[f], n, r, !0);
                    b.isEmptyObject(l) && (delete m.handle,
                        b._removeData(e, "events"))
                }
            },
            trigger: function(n, r, i, o) {
                var u, a, f, l, c, h, p, d = [i || s], v = g.call(n, "type") ? n.type : n, m = g.call(n, "namespace") ? n.namespace.split(".") : [];
                if (f = h = i = i || s,
                    3 !== i.nodeType && 8 !== i.nodeType && !nt.test(v + b.event.triggered) && (v.indexOf(".") >= 0 && (m = v.split("."),
                        v = m.shift(),
                        m.sort()),
                        a = 0 > v.indexOf(":") && "on" + v,
                        n = n[b.expando] ? n : new b.Event(v,"object" == typeof n && n),
                        n.isTrigger = !0,
                        n.namespace = m.join("."),
                        n.namespace_re = n.namespace ? RegExp("(^|\\.)" + m.join("\\.(?:.*\\.|)") + "(\\.|$)") : null,
                        n.result = t,
                    n.target || (n.target = i),
                        r = null == r ? [n] : b.makeArray(r, [n]),
                        c = b.event.special[v] || {},
                    o || !c.trigger || c.trigger.apply(i, r) !== !1)) {
                    if (!o && !c.noBubble && !b.isWindow(i)) {
                        for (l = c.delegateType || v,
                             nt.test(l + v) || (f = f.parentNode); f; f = f.parentNode)
                            d.push(f),
                                h = f;
                        h === (i.ownerDocument || s) && d.push(h.defaultView || h.parentWindow || e)
                    }
                    p = 0;
                    while ((f = d[p++]) && !n.isPropagationStopped())
                        n.type = p > 1 ? l : c.bindType || v,
                            u = (b._data(f, "events") || {})[n.type] && b._data(f, "handle"),
                        u && u.apply(f, r),
                            u = a && f[a],
                        u && b.acceptData(f) && u.apply && u.apply(f, r) === !1 && n.preventDefault();
                    if (n.type = v,
                            !(o || n.isDefaultPrevented() || c._default && c._default.apply(i.ownerDocument, r) !== !1 || "click" === v && b.nodeName(i, "a") || !b.acceptData(i) || !a || !i[v] || b.isWindow(i))) {
                        h = i[a],
                        h && (i[a] = null),
                            b.event.triggered = v;
                        try {
                            i[v]()
                        } catch (y) {}
                        b.event.triggered = t,
                        h && (i[a] = h)
                    }
                    return n.result
                }
            },
            dispatch: function(e) {
                e = b.event.fix(e);
                var n, r, i, s, o, u = [], a = d.call(arguments), f = (b._data(this, "events") || {})[e.type] || [], l = b.event.special[e.type] || {};
                if (a[0] = e,
                        e.delegateTarget = this,
                    !l.preDispatch || l.preDispatch.call(this, e) !== !1) {
                    u = b.event.handlers.call(this, e, f),
                        n = 0;
                    while ((s = u[n++]) && !e.isPropagationStopped()) {
                        e.currentTarget = s.elem,
                            o = 0;
                        while ((i = s.handlers[o++]) && !e.isImmediatePropagationStopped())
                            (!e.namespace_re || e.namespace_re.test(i.namespace)) && (e.handleObj = i,
                                e.data = i.data,
                                r = ((b.event.special[i.origType] || {}).handle || i.handler).apply(s.elem, a),
                            r !== t && (e.result = r) === !1 && (e.preventDefault(),
                                e.stopPropagation()))
                    }
                    return l.postDispatch && l.postDispatch.call(this, e),
                        e.result
                }
            },
            handlers: function(e, n) {
                var r, i, s, o, u = [], a = n.delegateCount, f = e.target;
                if (a && f.nodeType && (!e.button || "click" !== e.type))
                    for (; f != this; f = f.parentNode || this)
                        if (1 === f.nodeType && (f.disabled !== !0 || "click" !== e.type)) {
                            for (s = [],
                                     o = 0; a > o; o++)
                                i = n[o],
                                    r = i.selector + " ",
                                s[r] === t && (s[r] = i.needsContext ? b(r, this).index(f) >= 0 : b.find(r, this, null, [f]).length),
                                s[r] && s.push(i);
                            s.length && u.push({
                                elem: f,
                                handlers: s
                            })
                        }
                return n.length > a && u.push({
                    elem: this,
                    handlers: n.slice(a)
                }),
                    u
            },
            fix: function(e) {
                if (e[b.expando])
                    return e;
                var t, n, r, i = e.type, o = e, u = this.fixHooks[i];
                u || (this.fixHooks[i] = u = tt.test(i) ? this.mouseHooks : et.test(i) ? this.keyHooks : {}),
                    r = u.props ? this.props.concat(u.props) : this.props,
                    e = new b.Event(o),
                    t = r.length;
                while (t--)
                    n = r[t],
                        e[n] = o[n];
                return e.target || (e.target = o.srcElement || s),
                3 === e.target.nodeType && (e.target = e.target.parentNode),
                    e.metaKey = !!e.metaKey,
                    u.filter ? u.filter(e, o) : e
            },
            props: "altKey bubbles cancelable ctrlKey currentTarget eventPhase metaKey relatedTarget shiftKey target timeStamp view which".split(" "),
            fixHooks: {},
            keyHooks: {
                props: "char charCode key keyCode".split(" "),
                filter: function(e, t) {
                    return null == e.which && (e.which = null != t.charCode ? t.charCode : t.keyCode),
                        e
                }
            },
            mouseHooks: {
                props: "button buttons clientX clientY fromElement offsetX offsetY pageX pageY screenX screenY toElement".split(" "),
                filter: function(e, n) {
                    var r, i, o, u = n.button, a = n.fromElement;
                    return null == e.pageX && null != n.clientX && (i = e.target.ownerDocument || s,
                        o = i.documentElement,
                        r = i.body,
                        e.pageX = n.clientX + (o && o.scrollLeft || r && r.scrollLeft || 0) - (o && o.clientLeft || r && r.clientLeft || 0),
                        e.pageY = n.clientY + (o && o.scrollTop || r && r.scrollTop || 0) - (o && o.clientTop || r && r.clientTop || 0)),
                    !e.relatedTarget && a && (e.relatedTarget = a === e.target ? n.toElement : a),
                    e.which || u === t || (e.which = 1 & u ? 1 : 2 & u ? 3 : 4 & u ? 2 : 0),
                        e
                }
            },
            special: {
                load: {
                    noBubble: !0
                },
                click: {
                    trigger: function() {
                        return b.nodeName(this, "input") && "checkbox" === this.type && this.click ? (this.click(),
                            !1) : t
                    }
                },
                focus: {
                    trigger: function() {
                        if (this !== s.activeElement && this.focus)
                            try {
                                return this.focus(),
                                    !1
                            } catch (e) {}
                    },
                    delegateType: "focusin"
                },
                blur: {
                    trigger: function() {
                        return this === s.activeElement && this.blur ? (this.blur(),
                            !1) : t
                    },
                    delegateType: "focusout"
                },
                beforeunload: {
                    postDispatch: function(e) {
                        e.result !== t && (e.originalEvent.returnValue = e.result)
                    }
                }
            },
            simulate: function(e, t, n, r) {
                var i = b.extend(new b.Event, n, {
                    type: e,
                    isSimulated: !0,
                    originalEvent: {}
                });
                r ? b.event.trigger(i, null, t) : b.event.dispatch.call(t, i),
                i.isDefaultPrevented() && n.preventDefault()
            }
        },
            b.removeEvent = s.removeEventListener ? function(e, t, n) {
                e.removeEventListener && e.removeEventListener(t, n, !1)
            }
                : function(e, t, n) {
                var r = "on" + t;
                e.detachEvent && (typeof e[r] === i && (e[r] = null),
                    e.detachEvent(r, n))
            }
            ,
            b.Event = function(e, n) {
                return this instanceof b.Event ? (e && e.type ? (this.originalEvent = e,
                    this.type = e.type,
                    this.isDefaultPrevented = e.defaultPrevented || e.returnValue === !1 || e.getPreventDefault && e.getPreventDefault() ? it : st) : this.type = e,
                n && b.extend(this, n),
                    this.timeStamp = e && e.timeStamp || b.now(),
                    this[b.expando] = !0,
                    t) : new b.Event(e,n)
            }
            ,
            b.Event.prototype = {
                isDefaultPrevented: st,
                isPropagationStopped: st,
                isImmediatePropagationStopped: st,
                preventDefault: function() {
                    var e = this.originalEvent;
                    this.isDefaultPrevented = it,
                    e && (e.preventDefault ? e.preventDefault() : e.returnValue = !1)
                },
                stopPropagation: function() {
                    var e = this.originalEvent;
                    this.isPropagationStopped = it,
                    e && (e.stopPropagation && e.stopPropagation(),
                        e.cancelBubble = !0)
                },
                stopImmediatePropagation: function() {
                    this.isImmediatePropagationStopped = it,
                        this.stopPropagation()
                }
            },
            b.each({
                mouseenter: "mouseover",
                mouseleave: "mouseout"
            }, function(e, t) {
                b.event.special[e] = {
                    delegateType: t,
                    bindType: t,
                    handle: function(e) {
                        var n, r = this, i = e.relatedTarget, s = e.handleObj;
                        return (!i || i !== r && !b.contains(r, i)) && (e.type = s.origType,
                            n = s.handler.apply(this, arguments),
                            e.type = t),
                            n
                    }
                }
            }),
        b.support.submitBubbles || (b.event.special.submit = {
            setup: function() {
                return b.nodeName(this, "form") ? !1 : (b.event.add(this, "click._submit keypress._submit", function(e) {
                    var n = e.target
                        , r = b.nodeName(n, "input") || b.nodeName(n, "button") ? n.form : t;
                    r && !b._data(r, "submitBubbles") && (b.event.add(r, "submit._submit", function(e) {
                        e._submit_bubble = !0
                    }),
                        b._data(r, "submitBubbles", !0))
                }),
                    t)
            },
            postDispatch: function(e) {
                e._submit_bubble && (delete e._submit_bubble,
                this.parentNode && !e.isTrigger && b.event.simulate("submit", this.parentNode, e, !0))
            },
            teardown: function() {
                return b.nodeName(this, "form") ? !1 : (b.event.remove(this, "._submit"),
                    t)
            }
        }),
        b.support.changeBubbles || (b.event.special.change = {
            setup: function() {
                return Z.test(this.nodeName) ? (("checkbox" === this.type || "radio" === this.type) && (b.event.add(this, "propertychange._change", function(e) {
                    "checked" === e.originalEvent.propertyName && (this._just_changed = !0)
                }),
                    b.event.add(this, "click._change", function(e) {
                        this._just_changed && !e.isTrigger && (this._just_changed = !1),
                            b.event.simulate("change", this, e, !0)
                    })),
                    !1) : (b.event.add(this, "beforeactivate._change", function(e) {
                    var t = e.target;
                    Z.test(t.nodeName) && !b._data(t, "changeBubbles") && (b.event.add(t, "change._change", function(e) {
                        !this.parentNode || e.isSimulated || e.isTrigger || b.event.simulate("change", this.parentNode, e, !0)
                    }),
                        b._data(t, "changeBubbles", !0))
                }),
                    t)
            },
            handle: function(e) {
                var n = e.target;
                return this !== n || e.isSimulated || e.isTrigger || "radio" !== n.type && "checkbox" !== n.type ? e.handleObj.handler.apply(this, arguments) : t
            },
            teardown: function() {
                return b.event.remove(this, "._change"),
                    !Z.test(this.nodeName)
            }
        }),
        b.support.focusinBubbles || b.each({
            focus: "focusin",
            blur: "focusout"
        }, function(e, t) {
            var n = 0
                , r = function(e) {
                b.event.simulate(t, e.target, b.event.fix(e), !0)
            };
            b.event.special[t] = {
                setup: function() {
                    0 === n++ && s.addEventListener(e, r, !0)
                },
                teardown: function() {
                    0 === --n && s.removeEventListener(e, r, !0)
                }
            }
        }),
            b.fn.extend({
                on: function(e, n, r, i, s) {
                    var o, u;
                    if ("object" == typeof e) {
                        "string" != typeof n && (r = r || n,
                            n = t);
                        for (o in e)
                            this.on(o, n, r, e[o], s);
                        return this
                    }
                    if (null == r && null == i ? (i = n,
                            r = n = t) : null == i && ("string" == typeof n ? (i = r,
                            r = t) : (i = r,
                            r = n,
                            n = t)),
                        i === !1)
                        i = st;
                    else if (!i)
                        return this;
                    return 1 === s && (u = i,
                        i = function(e) {
                            return b().off(e),
                                u.apply(this, arguments)
                        }
                        ,
                        i.guid = u.guid || (u.guid = b.guid++)),
                        this.each(function() {
                            b.event.add(this, e, i, r, n)
                        })
                },
                one: function(e, t, n, r) {
                    return this.on(e, t, n, r, 1)
                },
                off: function(e, n, r) {
                    var i, s;
                    if (e && e.preventDefault && e.handleObj)
                        return i = e.handleObj,
                            b(e.delegateTarget).off(i.namespace ? i.origType + "." + i.namespace : i.origType, i.selector, i.handler),
                            this;
                    if ("object" == typeof e) {
                        for (s in e)
                            this.off(s, n, e[s]);
                        return this
                    }
                    return (n === !1 || "function" == typeof n) && (r = n,
                        n = t),
                    r === !1 && (r = st),
                        this.each(function() {
                            b.event.remove(this, e, r, n)
                        })
                },
                bind: function(e, t, n) {
                    return this.on(e, null, t, n)
                },
                unbind: function(e, t) {
                    return this.off(e, null, t)
                },
                delegate: function(e, t, n, r) {
                    return this.on(t, e, n, r)
                },
                undelegate: function(e, t, n) {
                    return 1 === arguments.length ? this.off(e, "**") : this.off(t, e || "**", n)
                },
                trigger: function(e, t) {
                    return this.each(function() {
                        b.event.trigger(e, t, this)
                    })
                },
                triggerHandler: function(e, n) {
                    var r = this[0];
                    return r ? b.event.trigger(e, n, r, !0) : t
                }
            }),
            function(e, t) {
                function rt(e) {
                    return J.test(e + "")
                }
                function it() {
                    var e, t = [];
                    return e = function(n, r) {
                        return t.push(n += " ") > i.cacheLength && delete e[t.shift()],
                            e[n] = r
                    }
                }
                function st(e) {
                    return e[w] = !0,
                        e
                }
                function ot(e) {
                    var t = c.createElement("div");
                    try {
                        return e(t)
                    } catch (n) {
                        return !1
                    } finally {
                        t = null
                    }
                }
                function ut(e, t, n, r) {
                    var i, s, o, u, a, f, h, v, m, y;
                    if ((t ? t.ownerDocument || t : E) !== c && l(t),
                            t = t || c,
                            n = n || [],
                        !e || "string" != typeof e)
                        return n;
                    if (1 !== (u = t.nodeType) && 9 !== u)
                        return [];
                    if (!p && !r) {
                        if (i = K.exec(e))
                            if (o = i[1]) {
                                if (9 === u) {
                                    if (s = t.getElementById(o),
                                        !s || !s.parentNode)
                                        return n;
                                    if (s.id === o)
                                        return n.push(s),
                                            n
                                } else if (t.ownerDocument && (s = t.ownerDocument.getElementById(o)) && g(t, s) && s.id === o)
                                    return n.push(s),
                                        n
                            } else {
                                if (i[2])
                                    return _.apply(n, D.call(t.getElementsByTagName(e), 0)),
                                        n;
                                if ((o = i[3]) && S.getByClassName && t.getElementsByClassName)
                                    return _.apply(n, D.call(t.getElementsByClassName(o), 0)),
                                        n
                            }
                        if (S.qsa && !d.test(e)) {
                            if (h = !0,
                                    v = w,
                                    m = t,
                                    y = 9 === u && e,
                                1 === u && "object" !== t.nodeName.toLowerCase()) {
                                f = ht(e),
                                    (h = t.getAttribute("id")) ? v = h.replace(Y, "\\$&") : t.setAttribute("id", v),
                                    v = "[id='" + v + "'] ",
                                    a = f.length;
                                while (a--)
                                    f[a] = v + pt(f[a]);
                                m = $.test(e) && t.parentNode || t,
                                    y = f.join(",")
                            }
                            if (y)
                                try {
                                    return _.apply(n, D.call(m.querySelectorAll(y), 0)),
                                        n
                                } catch (b) {} finally {
                                    h || t.removeAttribute("id")
                                }
                        }
                    }
                    return Et(e.replace(R, "$1"), t, n, r)
                }
                function at(e, t) {
                    var n = t && e
                        , r = n && (~t.sourceIndex || A) - (~e.sourceIndex || A);
                    if (r)
                        return r;
                    if (n)
                        while (n = n.nextSibling)
                            if (n === t)
                                return -1;
                    return e ? 1 : -1
                }
                function ft(e) {
                    return function(t) {
                        var n = t.nodeName.toLowerCase();
                        return "input" === n && t.type === e
                    }
                }
                function lt(e) {
                    return function(t) {
                        var n = t.nodeName.toLowerCase();
                        return ("input" === n || "button" === n) && t.type === e
                    }
                }
                function ct(e) {
                    return st(function(t) {
                        return t = +t,
                            st(function(n, r) {
                                var i, s = e([], n.length, t), o = s.length;
                                while (o--)
                                    n[i = s[o]] && (n[i] = !(r[i] = n[i]))
                            })
                    })
                }
                function ht(e, t) {
                    var n, r, s, o, u, a, f, l = C[e + " "];
                    if (l)
                        return t ? 0 : l.slice(0);
                    u = e,
                        a = [],
                        f = i.preFilter;
                    while (u) {
                        (!n || (r = U.exec(u))) && (r && (u = u.slice(r[0].length) || u),
                            a.push(s = [])),
                            n = !1,
                        (r = z.exec(u)) && (n = r.shift(),
                            s.push({
                                value: n,
                                type: r[0].replace(R, " ")
                            }),
                            u = u.slice(n.length));
                        for (o in i.filter)
                            !(r = V[o].exec(u)) || f[o] && !(r = f[o](r)) || (n = r.shift(),
                                s.push({
                                    value: n,
                                    type: o,
                                    matches: r
                                }),
                                u = u.slice(n.length));
                        if (!n)
                            break
                    }
                    return t ? u.length : u ? ut.error(e) : C(e, a).slice(0)
                }
                function pt(e) {
                    var t = 0
                        , n = e.length
                        , r = "";
                    for (; n > t; t++)
                        r += e[t].value;
                    return r
                }
                function dt(e, t, n) {
                    var i = t.dir
                        , s = n && "parentNode" === i
                        , o = T++;
                    return t.first ? function(t, n, r) {
                        while (t = t[i])
                            if (1 === t.nodeType || s)
                                return e(t, n, r)
                    }
                        : function(t, n, u) {
                        var a, f, l, c = x + " " + o;
                        if (u) {
                            while (t = t[i])
                                if ((1 === t.nodeType || s) && e(t, n, u))
                                    return !0
                        } else
                            while (t = t[i])
                                if (1 === t.nodeType || s)
                                    if (l = t[w] || (t[w] = {}),
                                        (f = l[i]) && f[0] === c) {
                                        if ((a = f[1]) === !0 || a === r)
                                            return a === !0
                                    } else if (f = l[i] = [c],
                                            f[1] = e(t, n, u) || r,
                                        f[1] === !0)
                                        return !0
                    }
                }
                function vt(e) {
                    return e.length > 1 ? function(t, n, r) {
                        var i = e.length;
                        while (i--)
                            if (!e[i](t, n, r))
                                return !1;
                        return !0
                    }
                        : e[0]
                }
                function mt(e, t, n, r, i) {
                    var s, o = [], u = 0, a = e.length, f = null != t;
                    for (; a > u; u++)
                        (s = e[u]) && (!n || n(s, r, i)) && (o.push(s),
                        f && t.push(u));
                    return o
                }
                function gt(e, t, n, r, i, s) {
                    return r && !r[w] && (r = gt(r)),
                    i && !i[w] && (i = gt(i, s)),
                        st(function(s, o, u, a) {
                            var f, l, c, h = [], p = [], d = o.length, v = s || wt(t || "*", u.nodeType ? [u] : u, []), m = !e || !s && t ? v : mt(v, h, e, u, a), g = n ? i || (s ? e : d || r) ? [] : o : m;
                            if (n && n(m, g, u, a),
                                    r) {
                                f = mt(g, p),
                                    r(f, [], u, a),
                                    l = f.length;
                                while (l--)
                                    (c = f[l]) && (g[p[l]] = !(m[p[l]] = c))
                            }
                            if (s) {
                                if (i || e) {
                                    if (i) {
                                        f = [],
                                            l = g.length;
                                        while (l--)
                                            (c = g[l]) && f.push(m[l] = c);
                                        i(null, g = [], f, a)
                                    }
                                    l = g.length;
                                    while (l--)
                                        (c = g[l]) && (f = i ? P.call(s, c) : h[l]) > -1 && (s[f] = !(o[f] = c))
                                }
                            } else
                                g = mt(g === o ? g.splice(d, g.length) : g),
                                    i ? i(null, o, g, a) : _.apply(o, g)
                        })
                }
                function yt(e) {
                    var t, n, r, s = e.length, o = i.relative[e[0].type], u = o || i.relative[" "], a = o ? 1 : 0, l = dt(function(e) {
                        return e === t
                    }, u, !0), c = dt(function(e) {
                        return P.call(t, e) > -1
                    }, u, !0), h = [function(e, n, r) {
                        return !o && (r || n !== f) || ((t = n).nodeType ? l(e, n, r) : c(e, n, r))
                    }
                    ];
                    for (; s > a; a++)
                        if (n = i.relative[e[a].type])
                            h = [dt(vt(h), n)];
                        else {
                            if (n = i.filter[e[a].type].apply(null, e[a].matches),
                                    n[w]) {
                                for (r = ++a; s > r; r++)
                                    if (i.relative[e[r].type])
                                        break;
                                return gt(a > 1 && vt(h), a > 1 && pt(e.slice(0, a - 1)).replace(R, "$1"), n, r > a && yt(e.slice(a, r)), s > r && yt(e = e.slice(r)), s > r && pt(e))
                            }
                            h.push(n)
                        }
                    return vt(h)
                }
                function bt(e, t) {
                    var n = 0
                        , s = t.length > 0
                        , o = e.length > 0
                        , u = function(u, a, l, h, p) {
                        var d, v, m, g = [], y = 0, b = "0", w = u && [], E = null != p, S = f, T = u || o && i.find.TAG("*", p && a.parentNode || a), N = x += null == S ? 1 : Math.random() || .1;
                        for (E && (f = a !== c && a,
                            r = n); null != (d = T[b]); b++) {
                            if (o && d) {
                                v = 0;
                                while (m = e[v++])
                                    if (m(d, a, l)) {
                                        h.push(d);
                                        break
                                    }
                                E && (x = N,
                                    r = ++n)
                            }
                            s && ((d = !m && d) && y--,
                            u && w.push(d))
                        }
                        if (y += b,
                            s && b !== y) {
                            v = 0;
                            while (m = t[v++])
                                m(w, g, a, l);
                            if (u) {
                                if (y > 0)
                                    while (b--)
                                        w[b] || g[b] || (g[b] = M.call(h));
                                g = mt(g)
                            }
                            _.apply(h, g),
                            E && !u && g.length > 0 && y + t.length > 1 && ut.uniqueSort(h)
                        }
                        return E && (x = N,
                            f = S),
                            w
                    };
                    return s ? st(u) : u
                }
                function wt(e, t, n) {
                    var r = 0
                        , i = t.length;
                    for (; i > r; r++)
                        ut(e, t[r], n);
                    return n
                }
                function Et(e, t, n, r) {
                    var s, o, a, f, l, c = ht(e);
                    if (!r && 1 === c.length) {
                        if (o = c[0] = c[0].slice(0),
                            o.length > 2 && "ID" === (a = o[0]).type && 9 === t.nodeType && !p && i.relative[o[1].type]) {
                            if (t = i.find.ID(a.matches[0].replace(et, tt), t)[0],
                                    !t)
                                return n;
                            e = e.slice(o.shift().value.length)
                        }
                        s = V.needsContext.test(e) ? 0 : o.length;
                        while (s--) {
                            if (a = o[s],
                                    i.relative[f = a.type])
                                break;
                            if ((l = i.find[f]) && (r = l(a.matches[0].replace(et, tt), $.test(o[0].type) && t.parentNode || t))) {
                                if (o.splice(s, 1),
                                        e = r.length && pt(o),
                                        !e)
                                    return _.apply(n, D.call(r, 0)),
                                        n;
                                break
                            }
                        }
                    }
                    return u(e, c)(r, t, p, n, $.test(e)),
                        n
                }
                function St() {}
                var n, r, i, s, o, u, a, f, l, c, h, p, d, v, m, g, y, w = "sizzle" + -(new Date), E = e.document, S = {}, x = 0, T = 0, N = it(), C = it(), k = it(), L = typeof t, A = 1 << 31, O = [], M = O.pop, _ = O.push, D = O.slice, P = O.indexOf || function(e) {
                        var t = 0
                            , n = this.length;
                        for (; n > t; t++)
                            if (this[t] === e)
                                return t;
                        return -1
                    }
                    , H = "[\\x20\\t\\r\\n\\f]", B = "(?:\\\\.|[\\w-]|[^\\x00-\\xa0])+", j = B.replace("w", "w#"), F = "([*^$|!~]?=)", I = "\\[" + H + "*(" + B + ")" + H + "*(?:" + F + H + "*(?:(['\"])((?:\\\\.|[^\\\\])*?)\\3|(" + j + ")|)|)" + H + "*\\]", q = ":(" + B + ")(?:\\(((['\"])((?:\\\\.|[^\\\\])*?)\\3|((?:\\\\.|[^\\\\()[\\]]|" + I.replace(3, 8) + ")*)|.*)\\)|)", R = RegExp("^" + H + "+|((?:^|[^\\\\])(?:\\\\.)*)" + H + "+$", "g"), U = RegExp("^" + H + "*," + H + "*"), z = RegExp("^" + H + "*([\\x20\\t\\r\\n\\f>+~])" + H + "*"), W = RegExp(q), X = RegExp("^" + j + "$"), V = {
                    ID: RegExp("^#(" + B + ")"),
                    CLASS: RegExp("^\\.(" + B + ")"),
                    NAME: RegExp("^\\[name=['\"]?(" + B + ")['\"]?\\]"),
                    TAG: RegExp("^(" + B.replace("w", "w*") + ")"),
                    ATTR: RegExp("^" + I),
                    PSEUDO: RegExp("^" + q),
                    CHILD: RegExp("^:(only|first|last|nth|nth-last)-(child|of-type)(?:\\(" + H + "*(even|odd|(([+-]|)(\\d*)n|)" + H + "*(?:([+-]|)" + H + "*(\\d+)|))" + H + "*\\)|)", "i"),
                    needsContext: RegExp("^" + H + "*[>+~]|:(even|odd|eq|gt|lt|nth|first|last)(?:\\(" + H + "*((?:-\\d)?\\d*)" + H + "*\\)|)(?=[^-]|$)", "i")
                }, $ = /[\x20\t\r\n\f]*[+~]/, J = /^[^{]+\{\s*\[native code/, K = /^(?:#([\w-]+)|(\w+)|\.([\w-]+))$/, Q = /^(?:input|select|textarea|button)$/i, G = /^h\d$/i, Y = /'|\\/g, Z = /\=[\x20\t\r\n\f]*([^'"\]]*)[\x20\t\r\n\f]*\]/g, et = /\\([\da-fA-F]{1,6}[\x20\t\r\n\f]?|.)/g, tt = function(e, t) {
                    var n = "0x" + t - 65536;
                    return n !== n ? t : 0 > n ? String.fromCharCode(n + 65536) : String.fromCharCode(55296 | n >> 10, 56320 | 1023 & n)
                };
                try {
                    D.call(E.documentElement.childNodes, 0)[0].nodeType
                } catch (nt) {
                    D = function(e) {
                        var t, n = [];
                        while (t = this[e++])
                            n.push(t);
                        return n
                    }
                }
                o = ut.isXML = function(e) {
                    var t = e && (e.ownerDocument || e).documentElement;
                    return t ? "HTML" !== t.nodeName : !1
                }
                    ,
                    l = ut.setDocument = function(e) {
                        var n = e ? e.ownerDocument || e : E;
                        return n !== c && 9 === n.nodeType && n.documentElement ? (c = n,
                            h = n.documentElement,
                            p = o(n),
                            S.tagNameNoComments = ot(function(e) {
                                return e.appendChild(n.createComment("")),
                                    !e.getElementsByTagName("*").length
                            }),
                            S.attributes = ot(function(e) {
                                e.innerHTML = "<select></select>";
                                var t = typeof e.lastChild.getAttribute("multiple");
                                return "boolean" !== t && "string" !== t
                            }),
                            S.getByClassName = ot(function(e) {
                                return e.innerHTML = "<div class='hidden e'></div><div class='hidden'></div>",
                                    e.getElementsByClassName && e.getElementsByClassName("e").length ? (e.lastChild.className = "e",
                                    2 === e.getElementsByClassName("e").length) : !1
                            }),
                            S.getByName = ot(function(e) {
                                e.id = w + 0,
                                    e.innerHTML = "<a name='" + w + "'></a><div name='" + w + "'></div>",
                                    h.insertBefore(e, h.firstChild);
                                var t = n.getElementsByName && n.getElementsByName(w).length === 2 + n.getElementsByName(w + 0).length;
                                return S.getIdNotName = !n.getElementById(w),
                                    h.removeChild(e),
                                    t
                            }),
                            i.attrHandle = ot(function(e) {
                                return e.innerHTML = "<a href='#'></a>",
                                e.firstChild && typeof e.firstChild.getAttribute !== L && "#" === e.firstChild.getAttribute("href")
                            }) ? {} : {
                                href: function(e) {
                                    return e.getAttribute("href", 2)
                                },
                                type: function(e) {
                                    return e.getAttribute("type")
                                }
                            },
                            S.getIdNotName ? (i.find.ID = function(e, t) {
                                    if (typeof t.getElementById !== L && !p) {
                                        var n = t.getElementById(e);
                                        return n && n.parentNode ? [n] : []
                                    }
                                }
                                    ,
                                    i.filter.ID = function(e) {
                                        var t = e.replace(et, tt);
                                        return function(e) {
                                            return e.getAttribute("id") === t
                                        }
                                    }
                            ) : (i.find.ID = function(e, n) {
                                    if (typeof n.getElementById !== L && !p) {
                                        var r = n.getElementById(e);
                                        return r ? r.id === e || typeof r.getAttributeNode !== L && r.getAttributeNode("id").value === e ? [r] : t : []
                                    }
                                }
                                    ,
                                    i.filter.ID = function(e) {
                                        var t = e.replace(et, tt);
                                        return function(e) {
                                            var n = typeof e.getAttributeNode !== L && e.getAttributeNode("id");
                                            return n && n.value === t
                                        }
                                    }
                            ),
                            i.find.TAG = S.tagNameNoComments ? function(e, n) {
                                return typeof n.getElementsByTagName !== L ? n.getElementsByTagName(e) : t
                            }
                                : function(e, t) {
                                var n, r = [], i = 0, s = t.getElementsByTagName(e);
                                if ("*" === e) {
                                    while (n = s[i++])
                                        1 === n.nodeType && r.push(n);
                                    return r
                                }
                                return s
                            }
                            ,
                            i.find.NAME = S.getByName && function(e, n) {
                                    return typeof n.getElementsByName !== L ? n.getElementsByName(name) : t
                                }
                            ,
                            i.find.CLASS = S.getByClassName && function(e, n) {
                                    return typeof n.getElementsByClassName === L || p ? t : n.getElementsByClassName(e)
                                }
                            ,
                            v = [],
                            d = [":focus"],
                        (S.qsa = rt(n.querySelectorAll)) && (ot(function(e) {
                            e.innerHTML = "<select><option selected=''></option></select>",
                            e.querySelectorAll("[selected]").length || d.push("\\[" + H + "*(?:checked|disabled|ismap|multiple|readonly|selected|value)"),
                            e.querySelectorAll(":checked").length || d.push(":checked")
                        }),
                            ot(function(e) {
                                e.innerHTML = "<input type='hidden' i=''/>",
                                e.querySelectorAll("[i^='']").length && d.push("[*^$]=" + H + "*(?:\"\"|'')"),
                                e.querySelectorAll(":enabled").length || d.push(":enabled", ":disabled"),
                                    e.querySelectorAll("*,:x"),
                                    d.push(",.*:")
                            })),
                        (S.matchesSelector = rt(m = h.matchesSelector || h.mozMatchesSelector || h.webkitMatchesSelector || h.oMatchesSelector || h.msMatchesSelector)) && ot(function(e) {
                            S.disconnectedMatch = m.call(e, "div"),
                                m.call(e, "[s!='']:x"),
                                v.push("!=", q)
                        }),
                            d = RegExp(d.join("|")),
                            v = RegExp(v.join("|")),
                            g = rt(h.contains) || h.compareDocumentPosition ? function(e, t) {
                                var n = 9 === e.nodeType ? e.documentElement : e
                                    , r = t && t.parentNode;
                                return e === r || !!r && 1 === r.nodeType && !!(n.contains ? n.contains(r) : e.compareDocumentPosition && 16 & e.compareDocumentPosition(r))
                            }
                                : function(e, t) {
                                if (t)
                                    while (t = t.parentNode)
                                        if (t === e)
                                            return !0;
                                return !1
                            }
                            ,
                            y = h.compareDocumentPosition ? function(e, t) {
                                var r;
                                return e === t ? (a = !0,
                                    0) : (r = t.compareDocumentPosition && e.compareDocumentPosition && e.compareDocumentPosition(t)) ? 1 & r || e.parentNode && 11 === e.parentNode.nodeType ? e === n || g(E, e) ? -1 : t === n || g(E, t) ? 1 : 0 : 4 & r ? -1 : 1 : e.compareDocumentPosition ? -1 : 1
                            }
                                : function(e, t) {
                                var r, i = 0, s = e.parentNode, o = t.parentNode, u = [e], f = [t];
                                if (e === t)
                                    return a = !0,
                                        0;
                                if (!s || !o)
                                    return e === n ? -1 : t === n ? 1 : s ? -1 : o ? 1 : 0;
                                if (s === o)
                                    return at(e, t);
                                r = e;
                                while (r = r.parentNode)
                                    u.unshift(r);
                                r = t;
                                while (r = r.parentNode)
                                    f.unshift(r);
                                while (u[i] === f[i])
                                    i++;
                                return i ? at(u[i], f[i]) : u[i] === E ? -1 : f[i] === E ? 1 : 0
                            }
                            ,
                            a = !1,
                            [0, 0].sort(y),
                            S.detectDuplicates = a,
                            c) : c
                    }
                    ,
                    ut.matches = function(e, t) {
                        return ut(e, null, null, t)
                    }
                    ,
                    ut.matchesSelector = function(e, t) {
                        if ((e.ownerDocument || e) !== c && l(e),
                                t = t.replace(Z, "='$1']"),
                                !(!S.matchesSelector || p || v && v.test(t) || d.test(t)))
                            try {
                                var n = m.call(e, t);
                                if (n || S.disconnectedMatch || e.document && 11 !== e.document.nodeType)
                                    return n
                            } catch (r) {}
                        return ut(t, c, null, [e]).length > 0
                    }
                    ,
                    ut.contains = function(e, t) {
                        return (e.ownerDocument || e) !== c && l(e),
                            g(e, t)
                    }
                    ,
                    ut.attr = function(e, t) {
                        var n;
                        return (e.ownerDocument || e) !== c && l(e),
                        p || (t = t.toLowerCase()),
                            (n = i.attrHandle[t]) ? n(e) : p || S.attributes ? e.getAttribute(t) : ((n = e.getAttributeNode(t)) || e.getAttribute(t)) && e[t] === !0 ? t : n && n.specified ? n.value : null
                    }
                    ,
                    ut.error = function(e) {
                        throw Error("Syntax error, unrecognized expression: " + e)
                    }
                    ,
                    ut.uniqueSort = function(e) {
                        var t, n = [], r = 1, i = 0;
                        if (a = !S.detectDuplicates,
                                e.sort(y),
                                a) {
                            for (; t = e[r]; r++)
                                t === e[r - 1] && (i = n.push(r));
                            while (i--)
                                e.splice(n[i], 1)
                        }
                        return e
                    }
                    ,
                    s = ut.getText = function(e) {
                        var t, n = "", r = 0, i = e.nodeType;
                        if (i) {
                            if (1 === i || 9 === i || 11 === i) {
                                if ("string" == typeof e.textContent)
                                    return e.textContent;
                                for (e = e.firstChild; e; e = e.nextSibling)
                                    n += s(e)
                            } else if (3 === i || 4 === i)
                                return e.nodeValue
                        } else
                            for (; t = e[r]; r++)
                                n += s(t);
                        return n
                    }
                    ,
                    i = ut.selectors = {
                        cacheLength: 50,
                        createPseudo: st,
                        match: V,
                        find: {},
                        relative: {
                            ">": {
                                dir: "parentNode",
                                first: !0
                            },
                            " ": {
                                dir: "parentNode"
                            },
                            "+": {
                                dir: "previousSibling",
                                first: !0
                            },
                            "~": {
                                dir: "previousSibling"
                            }
                        },
                        preFilter: {
                            ATTR: function(e) {
                                return e[1] = e[1].replace(et, tt),
                                    e[3] = (e[4] || e[5] || "").replace(et, tt),
                                "~=" === e[2] && (e[3] = " " + e[3] + " "),
                                    e.slice(0, 4)
                            },
                            CHILD: function(e) {
                                return e[1] = e[1].toLowerCase(),
                                    "nth" === e[1].slice(0, 3) ? (e[3] || ut.error(e[0]),
                                        e[4] = +(e[4] ? e[5] + (e[6] || 1) : 2 * ("even" === e[3] || "odd" === e[3])),
                                        e[5] = +(e[7] + e[8] || "odd" === e[3])) : e[3] && ut.error(e[0]),
                                    e
                            },
                            PSEUDO: function(e) {
                                var t, n = !e[5] && e[2];
                                return V.CHILD.test(e[0]) ? null : (e[4] ? e[2] = e[4] : n && W.test(n) && (t = ht(n, !0)) && (t = n.indexOf(")", n.length - t) - n.length) && (e[0] = e[0].slice(0, t),
                                    e[2] = n.slice(0, t)),
                                    e.slice(0, 3))
                            }
                        },
                        filter: {
                            TAG: function(e) {
                                return "*" === e ? function() {
                                    return !0
                                }
                                    : (e = e.replace(et, tt).toLowerCase(),
                                        function(t) {
                                            return t.nodeName && t.nodeName.toLowerCase() === e
                                        }
                                )
                            },
                            CLASS: function(e) {
                                var t = N[e + " "];
                                return t || (t = RegExp("(^|" + H + ")" + e + "(" + H + "|$)")) && N(e, function(e) {
                                        return t.test(e.className || typeof e.getAttribute !== L && e.getAttribute("class") || "")
                                    })
                            },
                            ATTR: function(e, t, n) {
                                return function(r) {
                                    var i = ut.attr(r, e);
                                    return null == i ? "!=" === t : t ? (i += "",
                                        "=" === t ? i === n : "!=" === t ? i !== n : "^=" === t ? n && 0 === i.indexOf(n) : "*=" === t ? n && i.indexOf(n) > -1 : "$=" === t ? n && i.slice(-n.length) === n : "~=" === t ? (" " + i + " ").indexOf(n) > -1 : "|=" === t ? i === n || i.slice(0, n.length + 1) === n + "-" : !1) : !0
                                }
                            },
                            CHILD: function(e, t, n, r, i) {
                                var s = "nth" !== e.slice(0, 3)
                                    , o = "last" !== e.slice(-4)
                                    , u = "of-type" === t;
                                return 1 === r && 0 === i ? function(e) {
                                    return !!e.parentNode
                                }
                                    : function(t, n, a) {
                                    var f, l, c, h, p, d, v = s !== o ? "nextSibling" : "previousSibling", m = t.parentNode, g = u && t.nodeName.toLowerCase(), y = !a && !u;
                                    if (m) {
                                        if (s) {
                                            while (v) {
                                                c = t;
                                                while (c = c[v])
                                                    if (u ? c.nodeName.toLowerCase() === g : 1 === c.nodeType)
                                                        return !1;
                                                d = v = "only" === e && !d && "nextSibling"
                                            }
                                            return !0
                                        }
                                        if (d = [o ? m.firstChild : m.lastChild],
                                            o && y) {
                                            l = m[w] || (m[w] = {}),
                                                f = l[e] || [],
                                                p = f[0] === x && f[1],
                                                h = f[0] === x && f[2],
                                                c = p && m.childNodes[p];
                                            while (c = ++p && c && c[v] || (h = p = 0) || d.pop())
                                                if (1 === c.nodeType && ++h && c === t) {
                                                    l[e] = [x, p, h];
                                                    break
                                                }
                                        } else if (y && (f = (t[w] || (t[w] = {}))[e]) && f[0] === x)
                                            h = f[1];
                                        else
                                            while (c = ++p && c && c[v] || (h = p = 0) || d.pop())
                                                if ((u ? c.nodeName.toLowerCase() === g : 1 === c.nodeType) && ++h && (y && ((c[w] || (c[w] = {}))[e] = [x, h]),
                                                    c === t))
                                                    break;
                                        return h -= i,
                                        h === r || 0 === h % r && h / r >= 0
                                    }
                                }
                            },
                            PSEUDO: function(e, t) {
                                var n, r = i.pseudos[e] || i.setFilters[e.toLowerCase()] || ut.error("unsupported pseudo: " + e);
                                return r[w] ? r(t) : r.length > 1 ? (n = [e, e, "", t],
                                        i.setFilters.hasOwnProperty(e.toLowerCase()) ? st(function(e, n) {
                                            var i, s = r(e, t), o = s.length;
                                            while (o--)
                                                i = P.call(e, s[o]),
                                                    e[i] = !(n[i] = s[o])
                                        }) : function(e) {
                                            return r(e, 0, n)
                                        }
                                ) : r
                            }
                        },
                        pseudos: {
                            not: st(function(e) {
                                var t = []
                                    , n = []
                                    , r = u(e.replace(R, "$1"));
                                return r[w] ? st(function(e, t, n, i) {
                                    var s, o = r(e, null, i, []), u = e.length;
                                    while (u--)
                                        (s = o[u]) && (e[u] = !(t[u] = s))
                                }) : function(e, i, s) {
                                    return t[0] = e,
                                        r(t, null, s, n),
                                        !n.pop()
                                }
                            }),
                            has: st(function(e) {
                                return function(t) {
                                    return ut(e, t).length > 0
                                }
                            }),
                            contains: st(function(e) {
                                return function(t) {
                                    return (t.textContent || t.innerText || s(t)).indexOf(e) > -1
                                }
                            }),
                            lang: st(function(e) {
                                return X.test(e || "") || ut.error("unsupported lang: " + e),
                                    e = e.replace(et, tt).toLowerCase(),
                                    function(t) {
                                        var n;
                                        do
                                            if (n = p ? t.getAttribute("xml:lang") || t.getAttribute("lang") : t.lang)
                                                return n = n.toLowerCase(),
                                                n === e || 0 === n.indexOf(e + "-");
                                        while ((t = t.parentNode) && 1 === t.nodeType);return !1
                                    }
                            }),
                            target: function(t) {
                                var n = e.location && e.location.hash;
                                return n && n.slice(1) === t.id
                            },
                            root: function(e) {
                                return e === h
                            },
                            focus: function(e) {
                                return e === c.activeElement && (!c.hasFocus || c.hasFocus()) && !!(e.type || e.href || ~e.tabIndex)
                            },
                            enabled: function(e) {
                                return e.disabled === !1
                            },
                            disabled: function(e) {
                                return e.disabled === !0
                            },
                            checked: function(e) {
                                var t = e.nodeName.toLowerCase();
                                return "input" === t && !!e.checked || "option" === t && !!e.selected
                            },
                            selected: function(e) {
                                return e.parentNode && e.parentNode.selectedIndex,
                                e.selected === !0
                            },
                            empty: function(e) {
                                for (e = e.firstChild; e; e = e.nextSibling)
                                    if (e.nodeName > "@" || 3 === e.nodeType || 4 === e.nodeType)
                                        return !1;
                                return !0
                            },
                            parent: function(e) {
                                return !i.pseudos.empty(e)
                            },
                            header: function(e) {
                                return G.test(e.nodeName)
                            },
                            input: function(e) {
                                return Q.test(e.nodeName)
                            },
                            button: function(e) {
                                var t = e.nodeName.toLowerCase();
                                return "input" === t && "button" === e.type || "button" === t
                            },
                            text: function(e) {
                                var t;
                                return "input" === e.nodeName.toLowerCase() && "text" === e.type && (null == (t = e.getAttribute("type")) || t.toLowerCase() === e.type)
                            },
                            first: ct(function() {
                                return [0]
                            }),
                            last: ct(function(e, t) {
                                return [t - 1]
                            }),
                            eq: ct(function(e, t, n) {
                                return [0 > n ? n + t : n]
                            }),
                            even: ct(function(e, t) {
                                var n = 0;
                                for (; t > n; n += 2)
                                    e.push(n);
                                return e
                            }),
                            odd: ct(function(e, t) {
                                var n = 1;
                                for (; t > n; n += 2)
                                    e.push(n);
                                return e
                            }),
                            lt: ct(function(e, t, n) {
                                var r = 0 > n ? n + t : n;
                                for (; --r >= 0; )
                                    e.push(r);
                                return e
                            }),
                            gt: ct(function(e, t, n) {
                                var r = 0 > n ? n + t : n;
                                for (; t > ++r; )
                                    e.push(r);
                                return e
                            })
                        }
                    };
                for (n in {
                    radio: !0,
                    checkbox: !0,
                    file: !0,
                    password: !0,
                    image: !0
                })
                    i.pseudos[n] = ft(n);
                for (n in {
                    submit: !0,
                    reset: !0
                })
                    i.pseudos[n] = lt(n);
                u = ut.compile = function(e, t) {
                    var n, r = [], i = [], s = k[e + " "];
                    if (!s) {
                        t || (t = ht(e)),
                            n = t.length;
                        while (n--)
                            s = yt(t[n]),
                                s[w] ? r.push(s) : i.push(s);
                        s = k(e, bt(i, r))
                    }
                    return s
                }
                    ,
                    i.pseudos.nth = i.pseudos.eq,
                    i.filters = St.prototype = i.pseudos,
                    i.setFilters = new St,
                    l(),
                    ut.attr = b.attr,
                    b.find = ut,
                    b.expr = ut.selectors,
                    b.expr[":"] = b.expr.pseudos,
                    b.unique = ut.uniqueSort,
                    b.text = ut.getText,
                    b.isXMLDoc = ut.isXML,
                    b.contains = ut.contains
            }(e);
        var ot = /Until$/
            , ut = /^(?:parents|prev(?:Until|All))/
            , at = /^.[^:#\[\.,]*$/
            , ft = b.expr.match.needsContext
            , lt = {
            children: !0,
            contents: !0,
            next: !0,
            prev: !0
        };
        b.fn.extend({
            find: function(e) {
                var t, n, r, i = this.length;
                if ("string" != typeof e)
                    return r = this,
                        this.pushStack(b(e).filter(function() {
                            for (t = 0; i > t; t++)
                                if (b.contains(r[t], this))
                                    return !0
                        }));
                for (n = [],
                         t = 0; i > t; t++)
                    b.find(e, this[t], n);
                return n = this.pushStack(i > 1 ? b.unique(n) : n),
                    n.selector = (this.selector ? this.selector + " " : "") + e,
                    n
            },
            has: function(e) {
                var t, n = b(e, this), r = n.length;
                return this.filter(function() {
                    for (t = 0; r > t; t++)
                        if (b.contains(this, n[t]))
                            return !0
                })
            },
            not: function(e) {
                return this.pushStack(ht(this, e, !1))
            },
            filter: function(e) {
                return this.pushStack(ht(this, e, !0))
            },
            is: function(e) {
                return !!e && ("string" == typeof e ? ft.test(e) ? b(e, this.context).index(this[0]) >= 0 : b.filter(e, this).length > 0 : this.filter(e).length > 0)
            },
            closest: function(e, t) {
                var n, r = 0, i = this.length, s = [], o = ft.test(e) || "string" != typeof e ? b(e, t || this.context) : 0;
                for (; i > r; r++) {
                    n = this[r];
                    while (n && n.ownerDocument && n !== t && 11 !== n.nodeType) {
                        if (o ? o.index(n) > -1 : b.find.matchesSelector(n, e)) {
                            s.push(n);
                            break
                        }
                        n = n.parentNode
                    }
                }
                return this.pushStack(s.length > 1 ? b.unique(s) : s)
            },
            index: function(e) {
                return e ? "string" == typeof e ? b.inArray(this[0], b(e)) : b.inArray(e.jquery ? e[0] : e, this) : this[0] && this[0].parentNode ? this.first().prevAll().length : -1
            },
            add: function(e, t) {
                var n = "string" == typeof e ? b(e, t) : b.makeArray(e && e.nodeType ? [e] : e)
                    , r = b.merge(this.get(), n);
                return this.pushStack(b.unique(r))
            },
            addBack: function(e) {
                return this.add(null == e ? this.prevObject : this.prevObject.filter(e))
            }
        }),
            b.fn.andSelf = b.fn.addBack,
            b.each({
                parent: function(e) {
                    var t = e.parentNode;
                    return t && 11 !== t.nodeType ? t : null
                },
                parents: function(e) {
                    return b.dir(e, "parentNode")
                },
                parentsUntil: function(e, t, n) {
                    return b.dir(e, "parentNode", n)
                },
                next: function(e) {
                    return ct(e, "nextSibling")
                },
                prev: function(e) {
                    return ct(e, "previousSibling")
                },
                nextAll: function(e) {
                    return b.dir(e, "nextSibling")
                },
                prevAll: function(e) {
                    return b.dir(e, "previousSibling")
                },
                nextUntil: function(e, t, n) {
                    return b.dir(e, "nextSibling", n)
                },
                prevUntil: function(e, t, n) {
                    return b.dir(e, "previousSibling", n)
                },
                siblings: function(e) {
                    return b.sibling((e.parentNode || {}).firstChild, e)
                },
                children: function(e) {
                    return b.sibling(e.firstChild)
                },
                contents: function(e) {
                    return b.nodeName(e, "iframe") ? e.contentDocument || e.contentWindow.document : b.merge([], e.childNodes)
                }
            }, function(e, t) {
                b.fn[e] = function(n, r) {
                    var i = b.map(this, t, n);
                    return ot.test(e) || (r = n),
                    r && "string" == typeof r && (i = b.filter(r, i)),
                        i = this.length > 1 && !lt[e] ? b.unique(i) : i,
                    this.length > 1 && ut.test(e) && (i = i.reverse()),
                        this.pushStack(i)
                }
            }),
            b.extend({
                filter: function(e, t, n) {
                    return n && (e = ":not(" + e + ")"),
                        1 === t.length ? b.find.matchesSelector(t[0], e) ? [t[0]] : [] : b.find.matches(e, t)
                },
                dir: function(e, n, r) {
                    var i = []
                        , s = e[n];
                    while (s && 9 !== s.nodeType && (r === t || 1 !== s.nodeType || !b(s).is(r)))
                        1 === s.nodeType && i.push(s),
                            s = s[n];
                    return i
                },
                sibling: function(e, t) {
                    var n = [];
                    for (; e; e = e.nextSibling)
                        1 === e.nodeType && e !== t && n.push(e);
                    return n
                }
            });
        var dt = "abbr|article|aside|audio|bdi|canvas|data|datalist|details|figcaption|figure|footer|header|hgroup|mark|meter|nav|output|progress|section|summary|time|video"
            , vt = / jQuery\d+="(?:null|\d+)"/g
            , mt = RegExp("<(?:" + dt + ")[\\s/>]", "i")
            , gt = /^\s+/
            , yt = /<(?!area|br|col|embed|hr|img|input|link|meta|param)(([\w:]+)[^>]*)\/>/gi
            , bt = /<([\w:]+)/
            , wt = /<tbody/i
            , Et = /<|&#?\w+;/
            , St = /<(?:script|style|link)/i
            , xt = /^(?:checkbox|radio)$/i
            , Tt = /checked\s*(?:[^=]|=\s*.checked.)/i
            , Nt = /^$|\/(?:java|ecma)script/i
            , Ct = /^true\/(.*)/
            , kt = /^\s*<!(?:\[CDATA\[|--)|(?:\]\]|--)>\s*$/g
            , Lt = {
            option: [1, "<select multiple='multiple'>", "</select>"],
            legend: [1, "<fieldset>", "</fieldset>"],
            area: [1, "<map>", "</map>"],
            param: [1, "<object>", "</object>"],
            thead: [1, "<table>", "</table>"],
            tr: [2, "<table><tbody>", "</tbody></table>"],
            col: [2, "<table><tbody></tbody><colgroup>", "</colgroup></table>"],
            td: [3, "<table><tbody><tr>", "</tr></tbody></table>"],
            _default: b.support.htmlSerialize ? [0, "", ""] : [1, "X<div>", "</div>"]
        }
            , At = pt(s)
            , Ot = At.appendChild(s.createElement("div"));
        Lt.optgroup = Lt.option,
            Lt.tbody = Lt.tfoot = Lt.colgroup = Lt.caption = Lt.thead,
            Lt.th = Lt.td,
            b.fn.extend({
                text: function(e) {
                    return b.access(this, function(e) {
                        return e === t ? b.text(this) : this.empty().append((this[0] && this[0].ownerDocument || s).createTextNode(e))
                    }, null, e, arguments.length)
                },
                wrapAll: function(e) {
                    if (b.isFunction(e))
                        return this.each(function(t) {
                            b(this).wrapAll(e.call(this, t))
                        });
                    if (this[0]) {
                        var t = b(e, this[0].ownerDocument).eq(0).clone(!0);
                        this[0].parentNode && t.insertBefore(this[0]),
                            t.map(function() {
                                var e = this;
                                while (e.firstChild && 1 === e.firstChild.nodeType)
                                    e = e.firstChild;
                                return e
                            }).append(this)
                    }
                    return this
                },
                wrapInner: function(e) {
                    return b.isFunction(e) ? this.each(function(t) {
                        b(this).wrapInner(e.call(this, t))
                    }) : this.each(function() {
                        var t = b(this)
                            , n = t.contents();
                        n.length ? n.wrapAll(e) : t.append(e)
                    })
                },
                wrap: function(e) {
                    var t = b.isFunction(e);
                    return this.each(function(n) {
                        b(this).wrapAll(t ? e.call(this, n) : e)
                    })
                },
                unwrap: function() {
                    return this.parent().each(function() {
                        b.nodeName(this, "body") || b(this).replaceWith(this.childNodes)
                    }).end()
                },
                append: function() {
                    return this.domManip(arguments, !0, function(e) {
                        (1 === this.nodeType || 11 === this.nodeType || 9 === this.nodeType) && this.appendChild(e)
                    })
                },
                prepend: function() {
                    return this.domManip(arguments, !0, function(e) {
                        (1 === this.nodeType || 11 === this.nodeType || 9 === this.nodeType) && this.insertBefore(e, this.firstChild)
                    })
                },
                before: function() {
                    return this.domManip(arguments, !1, function(e) {
                        this.parentNode && this.parentNode.insertBefore(e, this)
                    })
                },
                after: function() {
                    return this.domManip(arguments, !1, function(e) {
                        this.parentNode && this.parentNode.insertBefore(e, this.nextSibling)
                    })
                },
                remove: function(e, t) {
                    var n, r = 0;
                    for (; null != (n = this[r]); r++)
                        (!e || b.filter(e, [n]).length > 0) && (t || 1 !== n.nodeType || b.cleanData(jt(n)),
                        n.parentNode && (t && b.contains(n.ownerDocument, n) && Pt(jt(n, "script")),
                            n.parentNode.removeChild(n)));
                    return this
                },
                empty: function() {
                    var e, t = 0;
                    for (; null != (e = this[t]); t++) {
                        1 === e.nodeType && b.cleanData(jt(e, !1));
                        while (e.firstChild)
                            e.removeChild(e.firstChild);
                        e.options && b.nodeName(e, "select") && (e.options.length = 0)
                    }
                    return this
                },
                clone: function(e, t) {
                    return e = null == e ? !1 : e,
                        t = null == t ? e : t,
                        this.map(function() {
                            return b.clone(this, e, t)
                        })
                },
                html: function(e) {
                    return b.access(this, function(e) {
                        var n = this[0] || {}
                            , r = 0
                            , i = this.length;
                        if (e === t)
                            return 1 === n.nodeType ? n.innerHTML.replace(vt, "") : t;
                        if (!("string" != typeof e || St.test(e) || !b.support.htmlSerialize && mt.test(e) || !b.support.leadingWhitespace && gt.test(e) || Lt[(bt.exec(e) || ["", ""])[1].toLowerCase()])) {
                            e = e.replace(yt, "<$1></$2>");
                            try {
                                for (; i > r; r++)
                                    n = this[r] || {},
                                    1 === n.nodeType && (b.cleanData(jt(n, !1)),
                                        n.innerHTML = e);
                                n = 0
                            } catch (s) {}
                        }
                        n && this.empty().append(e)
                    }, null, e, arguments.length)
                },
                replaceWith: function(e) {
                    var t = b.isFunction(e);
                    return t || "string" == typeof e || (e = b(e).not(this).detach()),
                        this.domManip([e], !0, function(e) {
                            var t = this.nextSibling
                                , n = this.parentNode;
                            n && (b(this).remove(),
                                n.insertBefore(e, t))
                        })
                },
                detach: function(e) {
                    return this.remove(e, !0)
                },
                domManip: function(e, n, r) {
                    e = h.apply([], e);
                    var i, s, o, u, a, f, l = 0, c = this.length, p = this, d = c - 1, v = e[0], m = b.isFunction(v);
                    if (m || !(1 >= c || "string" != typeof v || b.support.checkClone) && Tt.test(v))
                        return this.each(function(i) {
                            var s = p.eq(i);
                            m && (e[0] = v.call(this, i, n ? s.html() : t)),
                                s.domManip(e, n, r)
                        });
                    if (c && (f = b.buildFragment(e, this[0].ownerDocument, !1, this),
                            i = f.firstChild,
                        1 === f.childNodes.length && (f = i),
                            i)) {
                        for (n = n && b.nodeName(i, "tr"),
                                 u = b.map(jt(f, "script"), _t),
                                 o = u.length; c > l; l++)
                            s = f,
                            l !== d && (s = b.clone(s, !0, !0),
                            o && b.merge(u, jt(s, "script"))),
                                r.call(n && b.nodeName(this[l], "table") ? Mt(this[l], "tbody") : this[l], s, l);
                        if (o)
                            for (a = u[u.length - 1].ownerDocument,
                                     b.map(u, Dt),
                                     l = 0; o > l; l++)
                                s = u[l],
                                Nt.test(s.type || "") && !b._data(s, "globalEval") && b.contains(a, s) && (s.src ? b.ajax({
                                    url: s.src,
                                    type: "GET",
                                    dataType: "script",
                                    async: !1,
                                    global: !1,
                                    "throws": !0
                                }) : b.globalEval((s.text || s.textContent || s.innerHTML || "").replace(kt, "")));
                        f = i = null
                    }
                    return this
                }
            }),
            b.each({
                appendTo: "append",
                prependTo: "prepend",
                insertBefore: "before",
                insertAfter: "after",
                replaceAll: "replaceWith"
            }, function(e, t) {
                b.fn[e] = function(e) {
                    var n, r = 0, i = [], s = b(e), o = s.length - 1;
                    for (; o >= r; r++)
                        n = r === o ? this : this.clone(!0),
                            b(s[r])[t](n),
                            p.apply(i, n.get());
                    return this.pushStack(i)
                }
            }),
            b.extend({
                clone: function(e, t, n) {
                    var r, i, s, o, u, a = b.contains(e.ownerDocument, e);
                    if (b.support.html5Clone || b.isXMLDoc(e) || !mt.test("<" + e.nodeName + ">") ? s = e.cloneNode(!0) : (Ot.innerHTML = e.outerHTML,
                            Ot.removeChild(s = Ot.firstChild)),
                            !(b.support.noCloneEvent && b.support.noCloneChecked || 1 !== e.nodeType && 11 !== e.nodeType || b.isXMLDoc(e)))
                        for (r = jt(s),
                                 u = jt(e),
                                 o = 0; null != (i = u[o]); ++o)
                            r[o] && Bt(i, r[o]);
                    if (t)
                        if (n)
                            for (u = u || jt(e),
                                     r = r || jt(s),
                                     o = 0; null != (i = u[o]); o++)
                                Ht(i, r[o]);
                        else
                            Ht(e, s);
                    return r = jt(s, "script"),
                    r.length > 0 && Pt(r, !a && jt(e, "script")),
                        r = u = i = null,
                        s
                },
                buildFragment: function(e, t, n, r) {
                    var i, s, o, u, a, f, l, c = e.length, h = pt(t), p = [], d = 0;
                    for (; c > d; d++)
                        if (s = e[d],
                            s || 0 === s)
                            if ("object" === b.type(s))
                                b.merge(p, s.nodeType ? [s] : s);
                            else if (Et.test(s)) {
                                u = u || h.appendChild(t.createElement("div")),
                                    a = (bt.exec(s) || ["", ""])[1].toLowerCase(),
                                    l = Lt[a] || Lt._default,
                                    u.innerHTML = l[1] + s.replace(yt, "<$1></$2>") + l[2],
                                    i = l[0];
                                while (i--)
                                    u = u.lastChild;
                                if (!b.support.leadingWhitespace && gt.test(s) && p.push(t.createTextNode(gt.exec(s)[0])),
                                        !b.support.tbody) {
                                    s = "table" !== a || wt.test(s) ? "<table>" !== l[1] || wt.test(s) ? 0 : u : u.firstChild,
                                        i = s && s.childNodes.length;
                                    while (i--)
                                        b.nodeName(f = s.childNodes[i], "tbody") && !f.childNodes.length && s.removeChild(f)
                                }
                                b.merge(p, u.childNodes),
                                    u.textContent = "";
                                while (u.firstChild)
                                    u.removeChild(u.firstChild);
                                u = h.lastChild
                            } else
                                p.push(t.createTextNode(s));
                    u && h.removeChild(u),
                    b.support.appendChecked || b.grep(jt(p, "input"), Ft),
                        d = 0;
                    while (s = p[d++])
                        if ((!r || -1 === b.inArray(s, r)) && (o = b.contains(s.ownerDocument, s),
                                u = jt(h.appendChild(s), "script"),
                            o && Pt(u),
                                n)) {
                            i = 0;
                            while (s = u[i++])
                                Nt.test(s.type || "") && n.push(s)
                        }
                    return u = null,
                        h
                },
                cleanData: function(e, t) {
                    var n, r, s, o, u = 0, a = b.expando, f = b.cache, c = b.support.deleteExpando, h = b.event.special;
                    for (; null != (n = e[u]); u++)
                        if ((t || b.acceptData(n)) && (s = n[a],
                                o = s && f[s])) {
                            if (o.events)
                                for (r in o.events)
                                    h[r] ? b.event.remove(n, r) : b.removeEvent(n, r, o.handle);
                            f[s] && (delete f[s],
                                c ? delete n[a] : typeof n.removeAttribute !== i ? n.removeAttribute(a) : n[a] = null,
                                l.push(s))
                        }
                }
            });
        var It, qt, Rt, Ut = /alpha\([^)]*\)/i, zt = /opacity\s*=\s*([^)]*)/, Wt = /^(top|right|bottom|left)$/, Xt = /^(none|table(?!-c[ea]).+)/, Vt = /^margin/, $t = RegExp("^(" + w + ")(.*)$", "i"), Jt = RegExp("^(" + w + ")(?!px)[a-z%]+$", "i"), Kt = RegExp("^([+-])=(" + w + ")", "i"), Qt = {
            BODY: "block"
        }, Gt = {
            position: "absolute",
            visibility: "hidden",
            display: "block"
        }, Yt = {
            letterSpacing: 0,
            fontWeight: 400
        }, Zt = ["Top", "Right", "Bottom", "Left"], en = ["Webkit", "O", "Moz", "ms"];
        b.fn.extend({
            css: function(e, n) {
                return b.access(this, function(e, n, r) {
                    var i, s, o = {}, u = 0;
                    if (b.isArray(n)) {
                        for (s = qt(e),
                                 i = n.length; i > u; u++)
                            o[n[u]] = b.css(e, n[u], !1, s);
                        return o
                    }
                    return r !== t ? b.style(e, n, r) : b.css(e, n)
                }, e, n, arguments.length > 1)
            },
            show: function() {
                return rn(this, !0)
            },
            hide: function() {
                return rn(this)
            },
            toggle: function(e) {
                var t = "boolean" == typeof e;
                return this.each(function() {
                    (t ? e : nn(this)) ? b(this).show() : b(this).hide()
                })
            }
        }),
            b.extend({
                cssHooks: {
                    opacity: {
                        get: function(e, t) {
                            if (t) {
                                var n = Rt(e, "opacity");
                                return "" === n ? "1" : n
                            }
                        }
                    }
                },
                cssNumber: {
                    columnCount: !0,
                    fillOpacity: !0,
                    fontWeight: !0,
                    lineHeight: !0,
                    opacity: !0,
                    orphans: !0,
                    widows: !0,
                    zIndex: !0,
                    zoom: !0
                },
                cssProps: {
                    "float": b.support.cssFloat ? "cssFloat" : "styleFloat"
                },
                style: function(e, n, r, i) {
                    if (e && 3 !== e.nodeType && 8 !== e.nodeType && e.style) {
                        var s, o, u, a = b.camelCase(n), f = e.style;
                        if (n = b.cssProps[a] || (b.cssProps[a] = tn(f, a)),
                                u = b.cssHooks[n] || b.cssHooks[a],
                            r === t)
                            return u && "get"in u && (s = u.get(e, !1, i)) !== t ? s : f[n];
                        if (o = typeof r,
                            "string" === o && (s = Kt.exec(r)) && (r = (s[1] + 1) * s[2] + parseFloat(b.css(e, n)),
                                o = "number"),
                                !(null == r || "number" === o && isNaN(r) || ("number" !== o || b.cssNumber[a] || (r += "px"),
                                b.support.clearCloneStyle || "" !== r || 0 !== n.indexOf("background") || (f[n] = "inherit"),
                                u && "set"in u && (r = u.set(e, r, i)) === t)))
                            try {
                                f[n] = r
                            } catch (l) {}
                    }
                },
                css: function(e, n, r, i) {
                    var s, o, u, a = b.camelCase(n);
                    return n = b.cssProps[a] || (b.cssProps[a] = tn(e.style, a)),
                        u = b.cssHooks[n] || b.cssHooks[a],
                    u && "get"in u && (o = u.get(e, !0, r)),
                    o === t && (o = Rt(e, n, i)),
                    "normal" === o && n in Yt && (o = Yt[n]),
                        "" === r || r ? (s = parseFloat(o),
                            r === !0 || b.isNumeric(s) ? s || 0 : o) : o
                },
                swap: function(e, t, n, r) {
                    var i, s, o = {};
                    for (s in t)
                        o[s] = e.style[s],
                            e.style[s] = t[s];
                    i = n.apply(e, r || []);
                    for (s in t)
                        e.style[s] = o[s];
                    return i
                }
            }),
            e.getComputedStyle ? (qt = function(t) {
                    return e.getComputedStyle(t, null)
                }
                    ,
                    Rt = function(e, n, r) {
                        var i, s, o, u = r || qt(e), a = u ? u.getPropertyValue(n) || u[n] : t, f = e.style;
                        return u && ("" !== a || b.contains(e.ownerDocument, e) || (a = b.style(e, n)),
                        Jt.test(a) && Vt.test(n) && (i = f.width,
                            s = f.minWidth,
                            o = f.maxWidth,
                            f.minWidth = f.maxWidth = f.width = a,
                            a = u.width,
                            f.width = i,
                            f.minWidth = s,
                            f.maxWidth = o)),
                            a
                    }
            ) : s.documentElement.currentStyle && (qt = function(e) {
                    return e.currentStyle
                }
                    ,
                    Rt = function(e, n, r) {
                        var i, s, o, u = r || qt(e), a = u ? u[n] : t, f = e.style;
                        return null == a && f && f[n] && (a = f[n]),
                        Jt.test(a) && !Wt.test(n) && (i = f.left,
                            s = e.runtimeStyle,
                            o = s && s.left,
                        o && (s.left = e.currentStyle.left),
                            f.left = "fontSize" === n ? "1em" : a,
                            a = f.pixelLeft + "px",
                            f.left = i,
                        o && (s.left = o)),
                            "" === a ? "auto" : a
                    }
            ),
            b.each(["height", "width"], function(e, n) {
                b.cssHooks[n] = {
                    get: function(e, r, i) {
                        return r ? 0 === e.offsetWidth && Xt.test(b.css(e, "display")) ? b.swap(e, Gt, function() {
                            return un(e, n, i)
                        }) : un(e, n, i) : t
                    },
                    set: function(e, t, r) {
                        var i = r && qt(e);
                        return sn(e, t, r ? on(e, n, r, b.support.boxSizing && "border-box" === b.css(e, "boxSizing", !1, i), i) : 0)
                    }
                }
            }),
        b.support.opacity || (b.cssHooks.opacity = {
            get: function(e, t) {
                return zt.test((t && e.currentStyle ? e.currentStyle.filter : e.style.filter) || "") ? .01 * parseFloat(RegExp.$1) + "" : t ? "1" : ""
            },
            set: function(e, t) {
                var n = e.style
                    , r = e.currentStyle
                    , i = b.isNumeric(t) ? "alpha(opacity=" + 100 * t + ")" : ""
                    , s = r && r.filter || n.filter || "";
                n.zoom = 1,
                (t >= 1 || "" === t) && "" === b.trim(s.replace(Ut, "")) && n.removeAttribute && (n.removeAttribute("filter"),
                "" === t || r && !r.filter) || (n.filter = Ut.test(s) ? s.replace(Ut, i) : s + " " + i)
            }
        }),
            b(function() {
                b.support.reliableMarginRight || (b.cssHooks.marginRight = {
                    get: function(e, n) {
                        return n ? b.swap(e, {
                            display: "inline-block"
                        }, Rt, [e, "marginRight"]) : t
                    }
                }),
                !b.support.pixelPosition && b.fn.position && b.each(["top", "left"], function(e, n) {
                    b.cssHooks[n] = {
                        get: function(e, r) {
                            return r ? (r = Rt(e, n),
                                Jt.test(r) ? b(e).position()[n] + "px" : r) : t
                        }
                    }
                })
            }),
        b.expr && b.expr.filters && (b.expr.filters.hidden = function(e) {
                return 0 >= e.offsetWidth && 0 >= e.offsetHeight || !b.support.reliableHiddenOffsets && "none" === (e.style && e.style.display || b.css(e, "display"))
            }
                ,
                b.expr.filters.visible = function(e) {
                    return !b.expr.filters.hidden(e)
                }
        ),
            b.each({
                margin: "",
                padding: "",
                border: "Width"
            }, function(e, t) {
                b.cssHooks[e + t] = {
                    expand: function(n) {
                        var r = 0
                            , i = {}
                            , s = "string" == typeof n ? n.split(" ") : [n];
                        for (; 4 > r; r++)
                            i[e + Zt[r] + t] = s[r] || s[r - 2] || s[0];
                        return i
                    }
                },
                Vt.test(e) || (b.cssHooks[e + t].set = sn)
            });
        var ln = /%20/g
            , cn = /\[\]$/
            , hn = /\r?\n/g
            , pn = /^(?:submit|button|image|reset|file)$/i
            , dn = /^(?:input|select|textarea|keygen)/i;
        b.fn.extend({
            serialize: function() {
                return b.param(this.serializeArray())
            },
            serializeArray: function() {
                return this.map(function() {
                    var e = b.prop(this, "elements");
                    return e ? b.makeArray(e) : this
                }).filter(function() {
                    var e = this.type;
                    return this.name && !b(this).is(":disabled") && dn.test(this.nodeName) && !pn.test(e) && (this.checked || !xt.test(e))
                }).map(function(e, t) {
                    var n = b(this).val();
                    return null == n ? null : b.isArray(n) ? b.map(n, function(e) {
                        return {
                            name: t.name,
                            value: e.replace(hn, "\r\n")
                        }
                    }) : {
                        name: t.name,
                        value: n.replace(hn, "\r\n")
                    }
                }).get()
            }
        }),
            b.param = function(e, n) {
                var r, i = [], s = function(e, t) {
                    t = b.isFunction(t) ? t() : null == t ? "" : t,
                        i[i.length] = encodeURIComponent(e) + "=" + encodeURIComponent(t)
                };
                if (n === t && (n = b.ajaxSettings && b.ajaxSettings.traditional),
                    b.isArray(e) || e.jquery && !b.isPlainObject(e))
                    b.each(e, function() {
                        s(this.name, this.value)
                    });
                else
                    for (r in e)
                        vn(r, e[r], n, s);
                return i.join("&").replace(ln, "+")
            }
            ,
            b.each("blur focus focusin focusout load resize scroll unload click dblclick mousedown mouseup mousemove mouseover mouseout mouseenter mouseleave change select submit keydown keypress keyup error contextmenu".split(" "), function(e, t) {
                b.fn[t] = function(e, n) {
                    return arguments.length > 0 ? this.on(t, null, e, n) : this.trigger(t)
                }
            }),
            b.fn.hover = function(e, t) {
                return this.mouseenter(e).mouseleave(t || e)
            }
        ;
        var mn, gn, yn = b.now(), bn = /\?/, wn = /#.*$/, En = /([?&])_=[^&]*/, Sn = /^(.*?):[ \t]*([^\r\n]*)\r?$/gm, xn = /^(?:about|app|app-storage|.+-extension|file|res|widget):$/, Tn = /^(?:GET|HEAD)$/, Nn = /^\/\//, Cn = /^([\w.+-]+:)(?:\/\/([^\/?#:]*)(?::(\d+)|)|)/, kn = b.fn.load, Ln = {}, An = {}, On = "*/".concat("*");
        try {
            gn = o.href
        } catch (Mn) {
            gn = s.createElement("a"),
                gn.href = "",
                gn = gn.href
        }
        mn = Cn.exec(gn.toLowerCase()) || [],
            b.fn.load = function(e, n, r) {
                if ("string" != typeof e && kn)
                    return kn.apply(this, arguments);
                var i, s, o, u = this, a = e.indexOf(" ");
                return a >= 0 && (i = e.slice(a, e.length),
                    e = e.slice(0, a)),
                    b.isFunction(n) ? (r = n,
                        n = t) : n && "object" == typeof n && (o = "POST"),
                u.length > 0 && b.ajax({
                    url: e,
                    type: o,
                    dataType: "html",
                    data: n
                }).done(function(e) {
                    s = arguments,
                        u.html(i ? b("<div>").append(b.parseHTML(e)).find(i) : e)
                }).complete(r && function(e, t) {
                        u.each(r, s || [e.responseText, t, e])
                    }
                ),
                    this
            }
            ,
            b.each(["ajaxStart", "ajaxStop", "ajaxComplete", "ajaxError", "ajaxSuccess", "ajaxSend"], function(e, t) {
                b.fn[t] = function(e) {
                    return this.on(t, e)
                }
            }),
            b.each(["get", "post"], function(e, n) {
                b[n] = function(e, r, i, s) {
                    return b.isFunction(r) && (s = s || i,
                        i = r,
                        r = t),
                        b.ajax({
                            url: e,
                            type: n,
                            dataType: s,
                            data: r,
                            success: i
                        })
                }
            }),
            b.extend({
                active: 0,
                lastModified: {},
                etag: {},
                ajaxSettings: {
                    url: gn,
                    type: "GET",
                    isLocal: xn.test(mn[1]),
                    global: !0,
                    processData: !0,
                    async: !0,
                    contentType: "application/x-www-form-urlencoded; charset=UTF-8",
                    accepts: {
                        "*": On,
                        text: "text/plain",
                        html: "text/html",
                        xml: "application/xml, text/xml",
                        json: "application/json, text/javascript"
                    },
                    contents: {
                        xml: /xml/,
                        html: /html/,
                        json: /json/
                    },
                    responseFields: {
                        xml: "responseXML",
                        text: "responseText"
                    },
                    converters: {
                        "* text": e.String,
                        "text html": !0,
                        "text json": b.parseJSON,
                        "text xml": b.parseXML
                    },
                    flatOptions: {
                        url: !0,
                        context: !0
                    }
                },
                ajaxSetup: function(e, t) {
                    return t ? Pn(Pn(e, b.ajaxSettings), t) : Pn(b.ajaxSettings, e)
                },
                ajaxPrefilter: _n(Ln),
                ajaxTransport: _n(An),
                ajax: function(e, n) {
                    function N(e, n, r, i) {
                        var l, g, y, E, S, T = n;
                        2 !== w && (w = 2,
                        u && clearTimeout(u),
                            f = t,
                            o = i || "",
                            x.readyState = e > 0 ? 4 : 0,
                        r && (E = Hn(c, x, r)),
                            e >= 200 && 300 > e || 304 === e ? (c.ifModified && (S = x.getResponseHeader("Last-Modified"),
                            S && (b.lastModified[s] = S),
                                S = x.getResponseHeader("etag"),
                            S && (b.etag[s] = S)),
                                204 === e ? (l = !0,
                                    T = "nocontent") : 304 === e ? (l = !0,
                                    T = "notmodified") : (l = Bn(c, E),
                                    T = l.state,
                                    g = l.data,
                                    y = l.error,
                                    l = !y)) : (y = T,
                            (e || !T) && (T = "error",
                            0 > e && (e = 0))),
                            x.status = e,
                            x.statusText = (n || T) + "",
                            l ? d.resolveWith(h, [g, T, x]) : d.rejectWith(h, [x, T, y]),
                            x.statusCode(m),
                            m = t,
                        a && p.trigger(l ? "ajaxSuccess" : "ajaxError", [x, c, l ? g : y]),
                            v.fireWith(h, [x, T]),
                        a && (p.trigger("ajaxComplete", [x, c]),
                        --b.active || b.event.trigger("ajaxStop")))
                    }
                    "object" == typeof e && (n = e,
                        e = t),
                        n = n || {};
                    var r, i, s, o, u, a, f, l, c = b.ajaxSetup({}, n), h = c.context || c, p = c.context && (h.nodeType || h.jquery) ? b(h) : b.event, d = b.Deferred(), v = b.Callbacks("once memory"), m = c.statusCode || {}, g = {}, y = {}, w = 0, S = "canceled", x = {
                        readyState: 0,
                        getResponseHeader: function(e) {
                            var t;
                            if (2 === w) {
                                if (!l) {
                                    l = {};
                                    while (t = Sn.exec(o))
                                        l[t[1].toLowerCase()] = t[2]
                                }
                                t = l[e.toLowerCase()]
                            }
                            return null == t ? null : t
                        },
                        getAllResponseHeaders: function() {
                            return 2 === w ? o : null
                        },
                        setRequestHeader: function(e, t) {
                            var n = e.toLowerCase();
                            return w || (e = y[n] = y[n] || e,
                                g[e] = t),
                                this
                        },
                        overrideMimeType: function(e) {
                            return w || (c.mimeType = e),
                                this
                        },
                        statusCode: function(e) {
                            var t;
                            if (e)
                                if (2 > w)
                                    for (t in e)
                                        m[t] = [m[t], e[t]];
                                else
                                    x.always(e[x.status]);
                            return this
                        },
                        abort: function(e) {
                            var t = e || S;
                            return f && f.abort(t),
                                N(0, t),
                                this
                        }
                    };
                    if (d.promise(x).complete = v.add,
                            x.success = x.done,
                            x.error = x.fail,
                            c.url = ((e || c.url || gn) + "").replace(wn, "").replace(Nn, mn[1] + "//"),
                            c.type = n.method || n.type || c.method || c.type,
                            c.dataTypes = b.trim(c.dataType || "*").toLowerCase().match(E) || [""],
                        null == c.crossDomain && (r = Cn.exec(c.url.toLowerCase()),
                            c.crossDomain = !(!r || r[1] === mn[1] && r[2] === mn[2] && (r[3] || ("http:" === r[1] ? 80 : 443)) == (mn[3] || ("http:" === mn[1] ? 80 : 443)))),
                        c.data && c.processData && "string" != typeof c.data && (c.data = b.param(c.data, c.traditional)),
                            Dn(Ln, c, n, x),
                        2 === w)
                        return x;
                    a = c.global,
                    a && 0 === b.active++ && b.event.trigger("ajaxStart"),
                        c.type = c.type.toUpperCase(),
                        c.hasContent = !Tn.test(c.type),
                        s = c.url,
                    c.hasContent || (c.data && (s = c.url += (bn.test(s) ? "&" : "?") + c.data,
                        delete c.data),
                    c.cache === !1 && (c.url = En.test(s) ? s.replace(En, "$1_=" + yn++) : s + (bn.test(s) ? "&" : "?") + "_=" + yn++)),
                    c.ifModified && (b.lastModified[s] && x.setRequestHeader("If-Modified-Since", b.lastModified[s]),
                    b.etag[s] && x.setRequestHeader("If-None-Match", b.etag[s])),
                    (c.data && c.hasContent && c.contentType !== !1 || n.contentType) && x.setRequestHeader("Content-Type", c.contentType),
                        x.setRequestHeader("Accept", c.dataTypes[0] && c.accepts[c.dataTypes[0]] ? c.accepts[c.dataTypes[0]] + ("*" !== c.dataTypes[0] ? ", " + On + "; q=0.01" : "") : c.accepts["*"]);
                    for (i in c.headers)
                        x.setRequestHeader(i, c.headers[i]);
                    if (!c.beforeSend || c.beforeSend.call(h, x, c) !== !1 && 2 !== w) {
                        S = "abort";
                        for (i in {
                            success: 1,
                            error: 1,
                            complete: 1
                        })
                            x[i](c[i]);
                        if (f = Dn(An, c, n, x)) {
                            x.readyState = 1,
                            a && p.trigger("ajaxSend", [x, c]),
                            c.async && c.timeout > 0 && (u = setTimeout(function() {
                                x.abort("timeout")
                            }, c.timeout));
                            try {
                                w = 1,
                                    f.send(g, N)
                            } catch (T) {
                                if (!(2 > w))
                                    throw T;
                                N(-1, T)
                            }
                        } else
                            N(-1, "No Transport");
                        return x
                    }
                    return x.abort()
                },
                getScript: function(e, n) {
                    return b.get(e, t, n, "script")
                },
                getJSON: function(e, t, n) {
                    return b.get(e, t, n, "json")
                }
            }),
            b.ajaxSetup({
                accepts: {
                    script: "text/javascript, application/javascript, application/ecmascript, application/x-ecmascript"
                },
                contents: {
                    script: /(?:java|ecma)script/
                },
                converters: {
                    "text script": function(e) {
                        return b.globalEval(e),
                            e
                    }
                }
            }),
            b.ajaxPrefilter("script", function(e) {
                e.cache === t && (e.cache = !1),
                e.crossDomain && (e.type = "GET",
                    e.global = !1)
            }),
            b.ajaxTransport("script", function(e) {
                if (e.crossDomain) {
                    var n, r = s.head || b("head")[0] || s.documentElement;
                    return {
                        send: function(t, i) {
                            n = s.createElement("script"),
                                n.async = !0,
                            e.scriptCharset && (n.charset = e.scriptCharset),
                                n.src = e.url,
                                n.onload = n.onreadystatechange = function(e, t) {
                                    (t || !n.readyState || /loaded|complete/.test(n.readyState)) && (n.onload = n.onreadystatechange = null,
                                    n.parentNode && n.parentNode.removeChild(n),
                                        n = null,
                                    t || i(200, "success"))
                                }
                                ,
                                r.insertBefore(n, r.firstChild)
                        },
                        abort: function() {
                            n && n.onload(t, !0)
                        }
                    }
                }
            });
        var jn = []
            , Fn = /(=)\?(?=&|$)|\?\?/;
        b.ajaxSetup({
            jsonp: "callback",
            jsonpCallback: function() {
                var e = jn.pop() || b.expando + "_" + yn++;
                return this[e] = !0,
                    e
            }
        }),
            b.ajaxPrefilter("json jsonp", function(n, r, i) {
                var s, o, u, a = n.jsonp !== !1 && (Fn.test(n.url) ? "url" : "string" == typeof n.data && !(n.contentType || "").indexOf("application/x-www-form-urlencoded") && Fn.test(n.data) && "data");
                return a || "jsonp" === n.dataTypes[0] ? (s = n.jsonpCallback = b.isFunction(n.jsonpCallback) ? n.jsonpCallback() : n.jsonpCallback,
                    a ? n[a] = n[a].replace(Fn, "$1" + s) : n.jsonp !== !1 && (n.url += (bn.test(n.url) ? "&" : "?") + n.jsonp + "=" + s),
                    n.converters["script json"] = function() {
                        return u || b.error(s + " was not called"),
                            u[0]
                    }
                    ,
                    n.dataTypes[0] = "json",
                    o = e[s],
                    e[s] = function() {
                        u = arguments
                    }
                    ,
                    i.always(function() {
                        e[s] = o,
                        n[s] && (n.jsonpCallback = r.jsonpCallback,
                            jn.push(s)),
                        u && b.isFunction(o) && o(u[0]),
                            u = o = t
                    }),
                    "script") : t
            });
        var In, qn, Rn = 0, Un = e.ActiveXObject && function() {
                    var e;
                    for (e in In)
                        In[e](t, !0)
                }
            ;
        b.ajaxSettings.xhr = e.ActiveXObject ? function() {
            return !this.isLocal && zn() || Wn()
        }
            : zn,
            qn = b.ajaxSettings.xhr(),
            b.support.cors = !!qn && "withCredentials"in qn,
            qn = b.support.ajax = !!qn,
        qn && b.ajaxTransport(function(n) {
            if (!n.crossDomain || b.support.cors) {
                var r;
                return {
                    send: function(i, s) {
                        var o, u, a = n.xhr();
                        if (n.username ? a.open(n.type, n.url, n.async, n.username, n.password) : a.open(n.type, n.url, n.async),
                                n.xhrFields)
                            for (u in n.xhrFields)
                                a[u] = n.xhrFields[u];
                        n.mimeType && a.overrideMimeType && a.overrideMimeType(n.mimeType),
                        n.crossDomain || i["X-Requested-With"] || (i["X-Requested-With"] = "XMLHttpRequest");
                        try {
                            for (u in i)
                                a.setRequestHeader(u, i[u])
                        } catch (f) {}
                        a.send(n.hasContent && n.data || null),
                            r = function(e, i) {
                                var u, f, l, c;
                                try {
                                    if (r && (i || 4 === a.readyState))
                                        if (r = t,
                                            o && (a.onreadystatechange = b.noop,
                                            Un && delete In[o]),
                                                i)
                                            4 !== a.readyState && a.abort();
                                        else {
                                            c = {},
                                                u = a.status,
                                                f = a.getAllResponseHeaders(),
                                            "string" == typeof a.responseText && (c.text = a.responseText);
                                            try {
                                                l = a.statusText
                                            } catch (h) {
                                                l = ""
                                            }
                                            u || !n.isLocal || n.crossDomain ? 1223 === u && (u = 204) : u = c.text ? 200 : 404
                                        }
                                } catch (p) {
                                    i || s(-1, p)
                                }
                                c && s(u, l, c, f)
                            }
                            ,
                            n.async ? 4 === a.readyState ? setTimeout(r) : (o = ++Rn,
                            Un && (In || (In = {},
                                b(e).unload(Un)),
                                In[o] = r),
                                a.onreadystatechange = r) : r()
                    },
                    abort: function() {
                        r && r(t, !0)
                    }
                }
            }
        });
        var Xn, Vn, $n = /^(?:toggle|show|hide)$/, Jn = RegExp("^(?:([+-])=|)(" + w + ")([a-z%]*)$", "i"), Kn = /queueHooks$/, Qn = [nr], Gn = {
            "*": [function(e, t) {
                var n, r, i = this.createTween(e, t), s = Jn.exec(t), o = i.cur(), u = +o || 0, a = 1, f = 20;
                if (s) {
                    if (n = +s[2],
                            r = s[3] || (b.cssNumber[e] ? "" : "px"),
                        "px" !== r && u) {
                        u = b.css(i.elem, e, !0) || n || 1;
                        do
                            a = a || ".5",
                                u /= a,
                                b.style(i.elem, e, u + r);
                        while (a !== (a = i.cur() / o) && 1 !== a && --f)
                    }
                    i.unit = r,
                        i.start = u,
                        i.end = s[1] ? u + (s[1] + 1) * n : n
                }
                return i
            }
            ]
        };
        b.Animation = b.extend(er, {
            tweener: function(e, t) {
                b.isFunction(e) ? (t = e,
                    e = ["*"]) : e = e.split(" ");
                var n, r = 0, i = e.length;
                for (; i > r; r++)
                    n = e[r],
                        Gn[n] = Gn[n] || [],
                        Gn[n].unshift(t)
            },
            prefilter: function(e, t) {
                t ? Qn.unshift(e) : Qn.push(e)
            }
        }),
            b.Tween = rr,
            rr.prototype = {
                constructor: rr,
                init: function(e, t, n, r, i, s) {
                    this.elem = e,
                        this.prop = n,
                        this.easing = i || "swing",
                        this.options = t,
                        this.start = this.now = this.cur(),
                        this.end = r,
                        this.unit = s || (b.cssNumber[n] ? "" : "px")
                },
                cur: function() {
                    var e = rr.propHooks[this.prop];
                    return e && e.get ? e.get(this) : rr.propHooks._default.get(this)
                },
                run: function(e) {
                    var t, n = rr.propHooks[this.prop];
                    return this.pos = t = this.options.duration ? b.easing[this.easing](e, this.options.duration * e, 0, 1, this.options.duration) : e,
                        this.now = (this.end - this.start) * t + this.start,
                    this.options.step && this.options.step.call(this.elem, this.now, this),
                        n && n.set ? n.set(this) : rr.propHooks._default.set(this),
                        this
                }
            },
            rr.prototype.init.prototype = rr.prototype,
            rr.propHooks = {
                _default: {
                    get: function(e) {
                        var t;
                        return null == e.elem[e.prop] || e.elem.style && null != e.elem.style[e.prop] ? (t = b.css(e.elem, e.prop, ""),
                            t && "auto" !== t ? t : 0) : e.elem[e.prop]
                    },
                    set: function(e) {
                        b.fx.step[e.prop] ? b.fx.step[e.prop](e) : e.elem.style && (null != e.elem.style[b.cssProps[e.prop]] || b.cssHooks[e.prop]) ? b.style(e.elem, e.prop, e.now + e.unit) : e.elem[e.prop] = e.now
                    }
                }
            },
            rr.propHooks.scrollTop = rr.propHooks.scrollLeft = {
                set: function(e) {
                    e.elem.nodeType && e.elem.parentNode && (e.elem[e.prop] = e.now)
                }
            },
            b.each(["toggle", "show", "hide"], function(e, t) {
                var n = b.fn[t];
                b.fn[t] = function(e, r, i) {
                    return null == e || "boolean" == typeof e ? n.apply(this, arguments) : this.animate(ir(t, !0), e, r, i)
                }
            }),
            b.fn.extend({
                fadeTo: function(e, t, n, r) {
                    return this.filter(nn).css("opacity", 0).show().end().animate({
                        opacity: t
                    }, e, n, r)
                },
                animate: function(e, t, n, r) {
                    var i = b.isEmptyObject(e)
                        , s = b.speed(t, n, r)
                        , o = function() {
                        var t = er(this, b.extend({}, e), s);
                        o.finish = function() {
                            t.stop(!0)
                        }
                            ,
                        (i || b._data(this, "finish")) && t.stop(!0)
                    };
                    return o.finish = o,
                        i || s.queue === !1 ? this.each(o) : this.queue(s.queue, o)
                },
                stop: function(e, n, r) {
                    var i = function(e) {
                        var t = e.stop;
                        delete e.stop,
                            t(r)
                    };
                    return "string" != typeof e && (r = n,
                        n = e,
                        e = t),
                    n && e !== !1 && this.queue(e || "fx", []),
                        this.each(function() {
                            var t = !0
                                , n = null != e && e + "queueHooks"
                                , s = b.timers
                                , o = b._data(this);
                            if (n)
                                o[n] && o[n].stop && i(o[n]);
                            else
                                for (n in o)
                                    o[n] && o[n].stop && Kn.test(n) && i(o[n]);
                            for (n = s.length; n--; )
                                s[n].elem !== this || null != e && s[n].queue !== e || (s[n].anim.stop(r),
                                    t = !1,
                                    s.splice(n, 1));
                            (t || !r) && b.dequeue(this, e)
                        })
                },
                finish: function(e) {
                    return e !== !1 && (e = e || "fx"),
                        this.each(function() {
                            var t, n = b._data(this), r = n[e + "queue"], i = n[e + "queueHooks"], s = b.timers, o = r ? r.length : 0;
                            for (n.finish = !0,
                                     b.queue(this, e, []),
                                 i && i.cur && i.cur.finish && i.cur.finish.call(this),
                                     t = s.length; t--; )
                                s[t].elem === this && s[t].queue === e && (s[t].anim.stop(!0),
                                    s.splice(t, 1));
                            for (t = 0; o > t; t++)
                                r[t] && r[t].finish && r[t].finish.call(this);
                            delete n.finish
                        })
                }
            }),
            b.each({
                slideDown: ir("show"),
                slideUp: ir("hide"),
                slideToggle: ir("toggle"),
                fadeIn: {
                    opacity: "show"
                },
                fadeOut: {
                    opacity: "hide"
                },
                fadeToggle: {
                    opacity: "toggle"
                }
            }, function(e, t) {
                b.fn[e] = function(e, n, r) {
                    return this.animate(t, e, n, r)
                }
            }),
            b.speed = function(e, t, n) {
                var r = e && "object" == typeof e ? b.extend({}, e) : {
                    complete: n || !n && t || b.isFunction(e) && e,
                    duration: e,
                    easing: n && t || t && !b.isFunction(t) && t
                };
                return r.duration = b.fx.off ? 0 : "number" == typeof r.duration ? r.duration : r.duration in b.fx.speeds ? b.fx.speeds[r.duration] : b.fx.speeds._default,
                (null == r.queue || r.queue === !0) && (r.queue = "fx"),
                    r.old = r.complete,
                    r.complete = function() {
                        b.isFunction(r.old) && r.old.call(this),
                        r.queue && b.dequeue(this, r.queue)
                    }
                    ,
                    r
            }
            ,
            b.easing = {
                linear: function(e) {
                    return e
                },
                swing: function(e) {
                    return .5 - Math.cos(e * Math.PI) / 2
                }
            },
            b.timers = [],
            b.fx = rr.prototype.init,
            b.fx.tick = function() {
                var e, n = b.timers, r = 0;
                for (Xn = b.now(); n.length > r; r++)
                    e = n[r],
                    e() || n[r] !== e || n.splice(r--, 1);
                n.length || b.fx.stop(),
                    Xn = t
            }
            ,
            b.fx.timer = function(e) {
                e() && b.timers.push(e) && b.fx.start()
            }
            ,
            b.fx.interval = 13,
            b.fx.start = function() {
                Vn || (Vn = setInterval(b.fx.tick, b.fx.interval))
            }
            ,
            b.fx.stop = function() {
                clearInterval(Vn),
                    Vn = null
            }
            ,
            b.fx.speeds = {
                slow: 600,
                fast: 200,
                _default: 400
            },
            b.fx.step = {},
        b.expr && b.expr.filters && (b.expr.filters.animated = function(e) {
                return b.grep(b.timers, function(t) {
                    return e === t.elem
                }).length
            }
        ),
            b.fn.offset = function(e) {
                if (arguments.length)
                    return e === t ? this : this.each(function(t) {
                        b.offset.setOffset(this, e, t)
                    });
                var n, r, s = {
                    top: 0,
                    left: 0
                }, o = this[0], u = o && o.ownerDocument;
                if (u)
                    return n = u.documentElement,
                        b.contains(n, o) ? (typeof o.getBoundingClientRect !== i && (s = o.getBoundingClientRect()),
                            r = sr(u),
                        {
                            top: s.top + (r.pageYOffset || n.scrollTop) - (n.clientTop || 0),
                            left: s.left + (r.pageXOffset || n.scrollLeft) - (n.clientLeft || 0)
                        }) : s
            }
            ,
            b.offset = {
                setOffset: function(e, t, n) {
                    var r = b.css(e, "position");
                    "static" === r && (e.style.position = "relative");
                    var i = b(e), s = i.offset(), o = b.css(e, "top"), u = b.css(e, "left"), a = ("absolute" === r || "fixed" === r) && b.inArray("auto", [o, u]) > -1, f = {}, l = {}, c, h;
                    a ? (l = i.position(),
                        c = l.top,
                        h = l.left) : (c = parseFloat(o) || 0,
                        h = parseFloat(u) || 0),
                    b.isFunction(t) && (t = t.call(e, n, s)),
                    null != t.top && (f.top = t.top - s.top + c),
                    null != t.left && (f.left = t.left - s.left + h),
                        "using"in t ? t.using.call(e, f) : i.css(f)
                }
            },
            b.fn.extend({
                position: function() {
                    if (this[0]) {
                        var e, t, n = {
                            top: 0,
                            left: 0
                        }, r = this[0];
                        return "fixed" === b.css(r, "position") ? t = r.getBoundingClientRect() : (e = this.offsetParent(),
                            t = this.offset(),
                        b.nodeName(e[0], "html") || (n = e.offset()),
                            n.top += b.css(e[0], "borderTopWidth", !0),
                            n.left += b.css(e[0], "borderLeftWidth", !0)),
                        {
                            top: t.top - n.top - b.css(r, "marginTop", !0),
                            left: t.left - n.left - b.css(r, "marginLeft", !0)
                        }
                    }
                },
                offsetParent: function() {
                    return this.map(function() {
                        var e = this.offsetParent || s.documentElement;
                        while (e && !b.nodeName(e, "html") && "static" === b.css(e, "position"))
                            e = e.offsetParent;
                        return e || s.documentElement
                    })
                }
            }),
            b.each({
                scrollLeft: "pageXOffset",
                scrollTop: "pageYOffset"
            }, function(e, n) {
                var r = /Y/.test(n);
                b.fn[e] = function(i) {
                    return b.access(this, function(e, i, s) {
                        var o = sr(e);
                        return s === t ? o ? n in o ? o[n] : o.document.documentElement[i] : e[i] : (o ? o.scrollTo(r ? b(o).scrollLeft() : s, r ? s : b(o).scrollTop()) : e[i] = s,
                            t)
                    }, e, i, arguments.length, null)
                }
            }),
            b.each({
                Height: "height",
                Width: "width"
            }, function(e, n) {
                b.each({
                    padding: "inner" + e,
                    content: n,
                    "": "outer" + e
                }, function(r, i) {
                    b.fn[i] = function(i, s) {
                        var o = arguments.length && (r || "boolean" != typeof i)
                            , u = r || (i === !0 || s === !0 ? "margin" : "border");
                        return b.access(this, function(n, r, i) {
                            var s;
                            return b.isWindow(n) ? n.document.documentElement["client" + e] : 9 === n.nodeType ? (s = n.documentElement,
                                Math.max(n.body["scroll" + e], s["scroll" + e], n.body["offset" + e], s["offset" + e], s["client" + e])) : i === t ? b.css(n, r, u) : b.style(n, r, i, u)
                        }, n, o ? i : t, o, null)
                    }
                })
            }),
            e.jQuery = e.$ = b,
        "function" == typeof define && define.amd && define.amd.jQuery && define("jquery", [], function() {
            return b
        })
    }(window),
    function(e) {
        if (typeof define == "function" && define.amd)
            define("js-cookie", e);
        else if (typeof exports == "object")
            module.exports = e();
        else {
            var t = window.Cookies
                , n = window.Cookies = e();
            n.noConflict = function() {
                return window.Cookies = t,
                    n
            }
        }
    }(function() {
        function e() {
            var e = 0
                , t = {};
            for (; e < arguments.length; e++) {
                var n = arguments[e];
                for (var r in n)
                    t[r] = n[r]
            }
            return t
        }
        function t(n) {
            function r(t, i, s) {
                var o;
                if (typeof document == "undefined")
                    return;
                if (arguments.length > 1) {
                    s = e({
                        path: "/"
                    }, r.defaults, s);
                    if (typeof s.expires == "number") {
                        var u = new Date;
                        u.setMilliseconds(u.getMilliseconds() + s.expires * 864e5),
                            s.expires = u
                    }
                    try {
                        o = JSON.stringify(i),
                        /^[\{\[]/.test(o) && (i = o)
                    } catch (a) {}
                    return n.write ? i = n.write(i, t) : i = encodeURIComponent(String(i)).replace(/%(23|24|26|2B|3A|3C|3E|3D|2F|3F|40|5B|5D|5E|60|7B|7D|7C)/g, decodeURIComponent),
                        t = encodeURIComponent(String(t)),
                        t = t.replace(/%(23|24|26|2B|5E|60|7C)/g, decodeURIComponent),
                        t = t.replace(/[\(\)]/g, escape),
                        document.cookie = [t, "=", i, s.expires && "; expires=" + s.expires.toUTCString(), s.path && "; path=" + s.path, s.domain && "; domain=" + s.domain, s.secure ? "; secure" : ""].join("")
                }
                t || (o = {});
                var f = document.cookie ? document.cookie.split("; ") : []
                    , l = /(%[0-9A-Z]{2})+/g
                    , c = 0;
                for (; c < f.length; c++) {
                    var h = f[c].split("=")
                        , p = h.slice(1).join("=");
                    p.charAt(0) === '"' && (p = p.slice(1, -1));
                    try {
                        var d = h[0].replace(l, decodeURIComponent);
                        p = n.read ? n.read(p, d) : n(p, d) || p.replace(l, decodeURIComponent);
                        if (this.json)
                            try {
                                p = JSON.parse(p)
                            } catch (a) {}
                        if (t === d) {
                            o = p;
                            break
                        }
                        t || (o[d] = p)
                    } catch (a) {}
                }
                return o
            }
            return r.set = r,
                r.get = function(e) {
                    return r(e)
                }
                ,
                r.getJSON = function() {
                    return r.apply({
                        json: !0
                    }, [].slice.call(arguments))
                }
                ,
                r.defaults = {},
                r.remove = function(t, n) {
                    r(t, "", e(n, {
                        expires: -1
                    }))
                }
                ,
                r.withConverter = t,
                r
        }
        return t(function() {})
    }),
    function() {
        var e = Array.prototype
            , t = Object.prototype
            , n = e.slice
            , r = t.toString
            , i = t.hasOwnProperty
            , s = window.console
            , o = window.navigator
            , u = window.document
            , a = o.userAgent
            , f = {}
            , l = "1.3.0"
            , c = "https:" == u.location.protocol ? "https://" : "http://"
            , h = window.XMLHttpRequest && "withCredentials"in new XMLHttpRequest
            , p = !1
            , d = {
            api_host: c + "analysis.qixin007.com/web_event/?method=web_event_srv.upload",
            debug: !1,
            ping: !1,
            ping_interval: 12e3,
            idle_timeout: 3e5,
            idle_threshold: 1e4,
            track_link_timeout: 300,
            cookie_expire_days: 365,
            cookie_cross_subdomain: !0,
            cookie_secure: !1,
            info_upload_interval_days: 7,
            session_interval_mins: 30,
            app_channel: "web",
            app_version: "1.0"
        };
        (function() {
            var t = e.forEach
                , s = Array.isArray
                , o = {};
            f.each = function(e, n, r) {
                if (e == null)
                    return;
                if (t && e.forEach === t)
                    e.forEach(n, r);
                else if (e.length === +e.length) {
                    for (var s = 0, u = e.length; s < u; s++)
                        if (s in e && n.call(r, e[s], s, e) === o)
                            return
                } else
                    for (var a in e)
                        if (i.call(e, a) && n.call(r, e[a], a, e) === o)
                            return
            }
                ,
                f.extend = function(e) {
                    return f.each(n.call(arguments, 1), function(t) {
                        for (var n in t)
                            t[n] !== void 0 && (e[n] = t[n])
                    }),
                        e
                }
                ,
                f.isUndefined = function(e) {
                    return e === void 0
                }
                ,
                f.isString = function(e) {
                    return r.call(e) == "[object String]"
                }
                ,
                f.isArray = s || function(e) {
                        return r.call(e) === "[object Array]"
                    }
                ,
                f.isFunction = function(e) {
                    try {
                        return /^\s*\bfunction\b/.test(e)
                    } catch (t) {
                        return !1
                    }
                }
                ,
                f.isObject = function(e) {
                    return e === Object(e) && !f.isArray(e)
                }
                ,
                f.includes = function(e, t) {
                    return e.indexOf(t) !== -1
                }
        })(),
            f.truncate = function(e, t) {
                var n;
                return typeof e == "string" ? n = e.slice(0, t) : f.isArray(e) ? (n = [],
                    f.each(e, function(e) {
                        n.push(f.truncate(e, t))
                    })) : f.isObject(e) ? (n = {},
                    f.each(e, function(e, r) {
                        n[r] = f.truncate(e, t)
                    })) : n = e,
                    n
            }
            ,
            f.strip_empty_properties = function(e) {
                var t = {};
                return f.each(e, function(e, n) {
                    f.isString(e) && e.length > 0 && (t[n] = e)
                }),
                    t
            }
            ,
            f.JSONEncode = function() {
                return function(e) {
                    var t, n = e, s, o = function(e) {
                        var t = /[\\\"\x00-\x1f\x7f-\x9f\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g
                            , n = {
                            "\b": "\\b",
                            "	": "\\t",
                            "\n": "\\n",
                            "\f": "\\f",
                            "\r": "\\r",
                            '"': '\\"',
                            "\\": "\\\\"
                        };
                        return t.lastIndex = 0,
                            t.test(e) ? '"' + e.replace(t, function(e) {
                                var t = n[e];
                                return typeof t == "string" ? t : "\\u" + ("0000" + e.charCodeAt(0).toString(16)).slice(-4)
                            }) + '"' : '"' + e + '"'
                    }, u = function(e, t) {
                        var n = ""
                            , s = "    "
                            , a = 0
                            , f = ""
                            , l = ""
                            , c = 0
                            , h = n
                            , p = []
                            , d = t[e];
                        d && typeof d == "object" && typeof d.toJSON == "function" && (d = d.toJSON(e));
                        switch (typeof d) {
                            case "string":
                                return o(d);
                            case "number":
                                return isFinite(d) ? String(d) : "null";
                            case "boolean":
                            case "null":
                                return String(d);
                            case "object":
                                if (!d)
                                    return "null";
                                n += s,
                                    p = [];
                                if (r.apply(d) === "[object Array]") {
                                    c = d.length;
                                    for (a = 0; a < c; a += 1)
                                        p[a] = u(a, d) || "null";
                                    return l = p.length === 0 ? "[]" : n ? "[\n" + n + p.join(",\n" + n) + "\n" + h + "]" : "[" + p.join(",") + "]",
                                        n = h,
                                        l
                                }
                                for (f in d)
                                    i.call(d, f) && (l = u(f, d),
                                    l && p.push(o(f) + (n ? ": " : ":") + l));
                                return l = p.length === 0 ? "{}" : n ? "{" + p.join(",") + "" + h + "}" : "{" + p.join(",") + "}",
                                    n = h,
                                    l
                        }
                    };
                    return u("", {
                        "": n
                    })
                }
            }(),
            f.JSONDecode = function() {
                var e, t, n = {
                    '"': '"',
                    "\\": "\\",
                    "/": "/",
                    b: "\b",
                    f: "\f",
                    n: "\n",
                    r: "\r",
                    t: "	"
                }, r, i = function(t) {
                    throw {
                        name: "SyntaxError",
                        message: t,
                        at: e,
                        text: r
                    }
                }, s = function(n) {
                    return n && n !== t && i("Expected '" + n + "' instead of '" + t + "'"),
                        t = r.charAt(e),
                        e += 1,
                        t
                }, o = function() {
                    var e, n = "";
                    t === "-" && (n = "-",
                        s("-"));
                    while (t >= "0" && t <= "9")
                        n += t,
                            s();
                    if (t === ".") {
                        n += ".";
                        while (s() && t >= "0" && t <= "9")
                            n += t
                    }
                    if (t === "e" || t === "E") {
                        n += t,
                            s();
                        if (t === "-" || t === "+")
                            n += t,
                                s();
                        while (t >= "0" && t <= "9")
                            n += t,
                                s()
                    }
                    e = +n;
                    if (!!isFinite(e))
                        return e;
                    i("Bad number")
                }, u = function() {
                    var e, r, o = "", u;
                    if (t === '"')
                        while (s()) {
                            if (t === '"')
                                return s(),
                                    o;
                            if (t === "\\") {
                                s();
                                if (t === "u") {
                                    u = 0;
                                    for (r = 0; r < 4; r += 1) {
                                        e = parseInt(s(), 16);
                                        if (!isFinite(e))
                                            break;
                                        u = u * 16 + e
                                    }
                                    o += String.fromCharCode(u)
                                } else {
                                    if (typeof n[t] != "string")
                                        break;
                                    o += n[t]
                                }
                            } else
                                o += t
                        }
                    i("Bad string")
                }, a = function() {
                    while (t && t <= " ")
                        s()
                }, f = function() {
                    switch (t) {
                        case "t":
                            return s("t"),
                                s("r"),
                                s("u"),
                                s("e"),
                                !0;
                        case "f":
                            return s("f"),
                                s("a"),
                                s("l"),
                                s("s"),
                                s("e"),
                                !1;
                        case "n":
                            return s("n"),
                                s("u"),
                                s("l"),
                                s("l"),
                                null
                    }
                    i("Unexpected '" + t + "'")
                }, l, c = function() {
                    var e = [];
                    if (t === "[") {
                        s("["),
                            a();
                        if (t === "]")
                            return s("]"),
                                e;
                        while (t) {
                            e.push(l()),
                                a();
                            if (t === "]")
                                return s("]"),
                                    e;
                            s(","),
                                a()
                        }
                    }
                    i("Bad array")
                }, h = function() {
                    var e, n = {};
                    if (t === "{") {
                        s("{"),
                            a();
                        if (t === "}")
                            return s("}"),
                                n;
                        while (t) {
                            e = u(),
                                a(),
                                s(":"),
                            Object.hasOwnProperty.call(n, e) && i('Duplicate key "' + e + '"'),
                                n[e] = l(),
                                a();
                            if (t === "}")
                                return s("}"),
                                    n;
                            s(","),
                                a()
                        }
                    }
                    i("Bad object")
                };
                return l = function() {
                    a();
                    switch (t) {
                        case "{":
                            return h();
                        case "[":
                            return c();
                        case '"':
                            return u();
                        case "-":
                            return o();
                        default:
                            return t >= "0" && t <= "9" ? o() : f()
                    }
                }
                    ,
                    function(n) {
                        var s;
                        return r = n,
                            e = 0,
                            t = " ",
                            s = l(),
                            a(),
                        t && i("Syntax error"),
                            s
                    }
            }(),
            f.HTTPBuildQuery = function(e, t) {
                var n, r, i, s = [];
                return typeof t == "undefined" && (t = "&"),
                    f.each(e, function(e, t) {
                        r = encodeURIComponent(e.toString()),
                            i = encodeURIComponent(t),
                            s[s.length] = i + "=" + r
                    }),
                    s.join(t)
            }
            ,
            f.getQueryParam = function(e, t) {
                t = t.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
                var n = "[\\?&#]" + t + "=([^&#]*)"
                    , r = new RegExp(n)
                    , i = r.exec(e);
                return i === null || i && typeof i[1] != "string" && i[1].length ? "" : decodeURIComponent(i[1]).replace(/\+/g, " ")
            }
            ,
            f.register_event = function() {
                function t(e, t, r) {
                    var i = function(i) {
                        i = i || n(window.event);
                        if (!i)
                            return undefined;
                        var s = !0, o, u;
                        f.isFunction(r) && (o = r(i)),
                            u = t.call(e, i);
                        if (!1 === o || !1 === u)
                            s = !1;
                        return s
                    };
                    return i
                }
                function n(e) {
                    return e && (e.preventDefault = n.preventDefault,
                        e.stopPropagation = n.stopPropagation),
                        e
                }
                var e = function(e, n, r, i) {
                    if (!e) {
                        v.error("No valid element provided to register_event");
                        return
                    }
                    if (e.addEventListener && !i)
                        e.addEventListener(n, r, !1);
                    else {
                        var s = "on" + n
                            , o = e[s];
                        e[s] = t(e, r, o)
                    }
                };
                return n.preventDefault = function() {
                    this.returnValue = !1
                }
                    ,
                    n.stopPropagation = function() {
                        this.cancelBubble = !0
                    }
                    ,
                    e
            }(),
            f.cookie = {
                get: function(e) {
                    var t = e + "="
                        , n = u.cookie.split(";");
                    for (var r = 0; r < n.length; r++) {
                        var i = n[r];
                        while (i.charAt(0) == " ")
                            i = i.substring(1, i.length);
                        if (i.indexOf(t) == 0)
                            return decodeURIComponent(i.substring(t.length, i.length))
                    }
                    return null
                },
                parse: function(e) {
                    var t;
                    try {
                        t = f.JSONDecode(f.cookie.get(e)) || {}
                    } catch (n) {}
                    return t
                },
                set: function(e, t, n, r, i) {
                    var s = ""
                        , o = ""
                        , a = "";
                    if (r) {
                        var f = u.location.hostname.match(/[a-z0-9][a-z0-9\-]+\.[a-z\.]{2,6}$/i)
                            , l = f ? f[0] : "";
                        s = l ? "; domain=." + l : ""
                    }
                    if (n) {
                        var c = new Date;
                        c.setTime(c.getTime() + n * 24 * 60 * 60 * 1e3),
                            o = "; expires=" + c.toGMTString()
                    }
                    i && (a = "; secure"),
                        u.cookie = e + "=" + encodeURIComponent(t) + o + "; path=/" + s + a
                },
                remove: function(e, t) {
                    f.cookie.set(e, "", -1, t)
                }
            },
            f.UUID = function() {
                var e = function() {
                    var e = 1 * new Date
                        , t = 0;
                    while (e == 1 * new Date)
                        t++;
                    return e.toString(16) + t.toString(16)
                }
                    , t = function() {
                    return Math.random().toString(16).replace(".", "")
                }
                    , n = function(e) {
                    function o(e, t) {
                        var n, r = 0;
                        for (n = 0; n < t.length; n++)
                            r |= i[n] << n * 8;
                        return e ^ r
                    }
                    var t = a, n, r, i = [], s = 0;
                    for (n = 0; n < t.length; n++)
                        r = t.charCodeAt(n),
                            i.unshift(r & 255),
                        i.length >= 4 && (s = o(s, i),
                            i = []);
                    return i.length > 0 && (s = o(s, i)),
                        s.toString(16)
                };
                return function() {
                    var r = (screen.height * screen.width).toString(16);
                    return e() + "-" + t() + "-" + n() + "-" + r + "-" + e()
                }
            }(),
            f.info = {
                campaignParams: function() {
                    var e = "utm_source utm_medium utm_campaign utm_content utm_term".split(" ")
                        , t = ""
                        , n = {};
                    return f.each(e, function(e) {
                        t = f.getQueryParam(u.URL, e),
                        t.length && (n[e] = t)
                    }),
                        n
                },
                searchEngine: function(e) {
                    return e.search("https?://(.*)google.([^/?]*)") === 0 ? "google" : e.search("https?://(.*)baidu.com") === 0 ? "baidu" : e.search("https?://(.*)sogou.com") === 0 ? "sogou" : e.search("https?://(.*)haosou.com") === 0 ? "haosou" : null
                },
                searchKeyword: function(e) {
                    var t = f.info.searchEngine(e);
                    return t == "google" ? f.getQueryParam(e, "q") : t == "baidu" ? f.getQueryParam(e, "wd") : t == "sogou" ? f.getQueryParam(e, "query") : t == "haosou" ? f.getQueryParam(e, "q") : null
                },
                referringDomain: function(e) {
                    var t = e.split("/");
                    return t.length >= 3 ? t[2] : ""
                },
                browser: function(e, t, n) {
                    var t = t || "";
                    return n ? f.includes(e, "Mini") ? "Opera Mini" : "Opera" : /(BlackBerry|PlayBook|BB10)/i.test(e) ? "BlackBerry" : f.includes(e, "FBIOS") ? "Facebook Mobile" : f.includes(e, "Chrome") ? "Chrome" : f.includes(e, "CriOS") ? "Chrome iOS" : f.includes(t, "Apple") ? f.includes(e, "Mobile") ? "Mobile Safari" : "Safari" : f.includes(e, "Android") ? "Android Mobile" : f.includes(e, "Konqueror") ? "Konqueror" : f.includes(e, "Firefox") ? "Firefox" : f.includes(e, "MSIE") || f.includes(e, "Trident/") ? "Internet Explorer" : f.includes(e, "Gecko") ? "Mozilla" : ""
                },
                os: function() {
                    var e = a;
                    return /Windows/i.test(e) ? /Phone/.test(e) ? "Windows Mobile" : "Windows" : /(iPhone|iPad|iPod)/.test(e) ? "iOS" : /Android/.test(e) ? "Android" : /(BlackBerry|PlayBook|BB10)/i.test(e) ? "BlackBerry" : /Mac/i.test(e) ? "Mac OS X" : /Linux/.test(e) ? "Linux" : ""
                },
                device: function(e) {
                    return /iPad/.test(e) ? "iPad" : /iPod/.test(e) ? "iPod Touch" : /iPhone/.test(e) ? "iPhone" : /(BlackBerry|PlayBook|BB10)/i.test(e) ? "BlackBerry" : /Windows Phone/i.test(e) ? "Windows Phone" : /Android/.test(e) ? "Android" : ""
                },
                resolution: function() {
                    return screen.width + "*" + screen.height
                },
                properties: function() {
                    var e = u.referrer
                        , t = location.search.split("zhuge_referrer=")[1];
                    return e = t ? decodeURIComponent(t) : e,
                        f.strip_empty_properties({
                            os: f.info.os(),
                            br: f.info.browser(a, o.vendor, window.opera),
                            dv: f.info.device(a),
                            rs: f.info.resolution(),
                            search: f.info.searchEngine(e),
                            keyword: f.info.searchKeyword(e),
                            url: u.URL,
                            referrer: e,
                            referrer_domain: f.info.referringDomain(e)
                        })
                }
            };
        var v = {
            log: function() {
                if (p && !f.isUndefined(s) && s)
                    try {
                        s.log.apply(s, arguments)
                    } catch (e) {
                        f.each(arguments, function(e) {
                            s.log(e)
                        })
                    }
            },
            error: function() {
                try {
                    if (p && !f.isUndefined(s) && s) {
                        var e = ["Zhuge error:"].concat(f.toArray(arguments));
                        try {
                            s.error.apply(s, e)
                        } catch (t) {
                            f.each(e, function(e) {
                                s.error(e)
                            })
                        }
                    }
                } catch (n) {}
            }
        }
            , m = function(e) {
            this.name = "_zg",
                this.props = {},
                this.config = f.extend({}, e),
                this.load()
        };
        m.prototype.load = function() {
            var e = f.cookie.parse(this.name);
            e && (this.props = f.extend({}, e))
        }
            ,
            m.prototype.save = function() {
                f.cookie.set(this.name, f.JSONEncode(this.props), this.config.cookie_expire_days, this.config.cookie_cross_subdomain, this.config.cookie_secure)
            }
            ,
            m.prototype.register_once = function(e, t) {
                return f.isObject(e) ? (typeof t == "undefined" && (t = "None"),
                    f.each(e, function(e, n) {
                        if (!this.props[n] || this.props[n] === t)
                            this.props[n] = e
                    }, this),
                    this.save(),
                    !0) : !1
            }
            ,
            m.prototype.register = function(e) {
                return f.isObject(e) ? (f.extend(this.props, e),
                    this.save(),
                    !0) : !1
            }
        ;
        var g = function() {
            this.config = {},
                f.extend(this.config, d),
                this.idle = 0,
                this.last_activity = new Date
        };
        g.prototype._init = function(e, t) {
            this._key = e,
                this._jsc = function() {}
                ,
            f.isObject(t) && (f.extend(this.config, t),
                p = p || this.config.debug),
                this.cookie = new m(this.config),
                this.cookie.register_once({
                    uuid: f.UUID(),
                    sid: 0,
                    updated: 0,
                    info: 0
                }, ""),
                this._session(),
                this._info(),
                this._startPing()
        }
            ,
            g.prototype._session = function() {
                var e = this.cookie.props.updated
                    , t = this.cookie.props.sid
                    , n = 1 * new Date / 1e3;
                if (t == 0 || n > e + this.config.session_interval_mins * 60) {
                    if (t > 0 && e > 0) {
                        var r = {};
                        r.et = "se",
                            r.sid = t,
                            r.dr = Math.round((e - t) * 1e3) / 1e3,
                            this._batchTrack(r)
                    }
                    t = n;
                    var i = {};
                    i.et = "ss",
                        i.sid = t,
                        i.cn = this.config.app_channel,
                        i.vn = this.config.app_version,
                        i.pr = f.extend(f.info.properties(), f.info.campaignParams()),
                        this._batchTrack(i),
                        this.cookie.register({
                            sid: t
                        }, "")
                }
                this.cookie.register({
                    updated: n
                }, "")
            }
            ,
            g.prototype._info = function() {
                var e = this.cookie.props.info
                    , t = 1 * new Date;
                if (t > e + this.config.info_upload_interval_days * 24 * 60 * 60 * 1e3) {
                    var n = {};
                    n.et = "info",
                        n.pr = f.extend(f.info.properties(), {
                            cn: this.config.app_channel,
                            vn: this.config.app_version
                        }),
                        this._batchTrack(n),
                        this.cookie.register({
                            info: t
                        }, "")
                }
            }
            ,
            g.prototype.debug = function(e) {
                p = e
            }
            ,
            g.prototype.identify = function(e, t, n) {
                this.cookie.register({
                    cuid: e
                }, ""),
                    this._session();
                var r = {};
                r.et = "idf",
                    r.cuid = e,
                    r.pr = t,
                    r.sid = this.cookie.props.sid,
                    this._batchTrack(r, n)
            }
            ,
            g.prototype.page = function(e, t) {
                this._session();
                var n = u.location.href
                    , r = {};
                r.et = "pg",
                    r.pid = n,
                    r.pn = typeof e == "undefined" ? n : e,
                    r.tl = u.title,
                    r.ref = u.referrer,
                    r.sid = this.cookie.props.sid,
                    this._batchTrack(r, t)
            }
            ,
            g.prototype.track = function(e, t, n, r) {
                if (r) {
                    this._session();
                    var i = {};
                    i.et = "cus",
                        i.eid = e,
                        i.pr = t,
                        i.ts = (r ? r : new Date).getTime() / 1e3,
                        i.sid = this.cookie.props.sid,
                        this._omitPatchTrack(i, r, n)
                } else {
                    this._session();
                    var i = {};
                    i.et = "cus",
                        i.eid = e,
                        i.pr = t,
                        i.ts = (new Date).getTime() / 1e3,
                        i.sid = this.cookie.props.sid,
                        this._batchTrack(i, n)
                }
            }
            ,
            g.prototype.trackLink = function(e, t, n) {
                if (!e)
                    return this;
                f.isArray(e) || (e = [e]);
                var r = this;
                return f.each(e, function(e) {
                    var i = function(i) {
                        r.track(t, n),
                        e.href && e.target !== "_blank" && !i.metaKey && i.which !== 2 && (i.preventDefault(),
                            window.setTimeout(function() {
                                window.location.href = e.href
                            }, this.config.track_link_timeout))
                    };
                    f.register_event(e, "click", i)
                }),
                    this
            }
            ,
            g.prototype.trackForm = function(e, t, n) {
                if (!e)
                    return this;
                f.isArray(e) || (e = [e]);
                var r = this;
                return f.each(e, function(e) {
                    var i = function(i) {
                        i.preventDefault(),
                            r.track(t, n),
                            window.setTimeout(function() {
                                e.submit()
                            }, TRACK_LINK_TIMEOUT)
                    }
                        , s = window.jQuery || window.Zepto;
                    s ? s(e).submit(i) : f.register_event(e, "submit", i)
                }),
                    this
            }
            ,
            g.prototype._moved = function(e) {
                this.last_activity = new Date,
                    this.idle = 0
            }
            ,
            g.prototype._startPing = function() {
                var e = this;
                f.register_event(window, "mousemove", function() {
                    e._moved.apply(e, arguments)
                }),
                typeof this.pingInterval == "undefined" && (this.pingInterval = window.setInterval(function() {
                    e._ping()
                }, this.config.ping_interval))
            }
            ,
            g.prototype._stopPing = function() {
                typeof this.pingInterval != "undefined" && (window.clearInterval(this.pingInterval),
                    delete this.pingInterval)
            }
            ,
            g.prototype._ping = function() {
                if (this.config.ping && this.idle < this.config.idle_timeout) {
                    var e = {};
                    e.type = "ping",
                        e.sdk = "web",
                        e.sdkv = l,
                        e.ak = this._key,
                        e.did = this.cookie.props.uuid,
                        e.cuid = this.cookie.props.cuid,
                        this._sendTrackRequest(e)
                } else
                    this._stopPing();
                var t = new Date;
                return t - this.last_activity > this.config.idle_threshold && (this.idle = t - this.last_activity),
                    this
            }
            ,
            g.prototype.getDid = function() {
                return this.cookie.props.uuid
            }
            ,
            g.prototype._batchTrack = function(e, t) {
                var n = {};
                n.type = "statis",
                    n.sdk = "web",
                    n.sdkv = l,
                    n.cn = this.config.app_channel,
                    n.vn = this.config.app_version,
                    n.ak = this._key,
                    n.did = this.cookie.props.uuid,
                    n.cuid = this.cookie.props.cuid,
                    n.ts = 1 * new Date / 1e3,
                f.cookie.get("responseTimeline") && (n.lt = parseInt(f.cookie.get("responseTimeline"))),
                this.config.debug && (n.debug = 1);
                var r = [];
                r.push(e),
                    n.data = r,
                    this._sendTrackRequest(n, this._prepareCallback(t, n))
            }
            ,
            g.prototype._omitPatchTrack = function(e, t, n) {
                var r = {};
                r.type = "statis",
                    r.sdk = "web",
                    r.sdkv = l,
                    r.cn = this.config.app_channel,
                    r.vn = this.config.app_version,
                    r.ak = this._key,
                    r.did = this.cookie.props.uuid,
                    r.cuid = this.cookie.props.cuid,
                    r.ts = (t ? t.getTime() : (new Date).getTime()) / 1e3,
                f.cookie.get("responseTimeline") && (r.lt = parseInt(f.cookie.get("responseTimeline"))),
                this.config.debug && (r.debug = 1);
                var i = [];
                i.push(e),
                    r.data = i,
                    this._sendTrackRequest(r, this._prepareCallback(n, r))
            }
            ,
            g.prototype._prepareCallback = function(e, t) {
                if (f.isUndefined(e))
                    return null;
                var n = function(n) {
                    e(n, t)
                };
                return n
            }
            ,
            g.prototype._sendTrackRequest = function(e, t) {
                var n = f.truncate(e, 255)
                    , r = f.JSONEncode(n);
                data = {
                    event: r,
                    _: (new Date).getTime().toString()
                };
                var i = this.config.api_host + "&" + f.HTTPBuildQuery(data);
                this._sendRequest(i, t)
            }
            ,
            g.prototype._sendRequest = function(e, t) {
                var n = (new Date).getTime()
                    , r = new Image;
                r.beginSendTimestamp = (new Date).getTime(),
                    r.finished = !1;
                var i = function() {
                    var e = r;
                    setTimeout(function() {
                        e.finished || (e.finished = !0,
                            f.cookie.set("responseTimeline", (new Date).getTime() - e.beginSendTimestamp),
                        t && t())
                    }, 3e3)
                };
                r.onload = function() {
                    var e = r;
                    e.finished || (e.finished = !0,
                        f.cookie.set("responseTimeline", (new Date).getTime() - e.beginSendTimestamp),
                    t && t())
                }
                    ,
                    r.onerror = function(e) {
                        var n = r;
                        n.finished || (n.finished = !0,
                            f.cookie.set("responseTimeline", (new Date).getTime() - n.beginSendTimestamp),
                        t && t())
                    }
                    ,
                    r.src = e,
                    i()
            }
            ,
            g.prototype.push = function(e) {
                var t = e.shift();
                if (!this[t])
                    return;
                this[t].apply(this, e)
            }
            ,
            g.prototype.getKey = function() {
                return this._key
            }
        ;
        var y = window.zhuge || []
            , b = new g;
        while (y && y.length > 0) {
            var w = y.shift()
                , E = w.shift();
            b[E] && b[E].apply(b, w)
        }
        window.zhuge = b
    }(),
    define("zhuge", function(e) {
        return function() {
            var t, n;
            return t || e.zhuge
        }
    }(this)),
    define("statistics", ["zhuge"], function(e) {
        e._init("4a3c39b4fa2a4a6db47636b4fa684725");
        var t = function(t, n, r) {
            r ? e.identify(t, n, r) : e.identify(t, n)
        }
            , n = function(t, n, r) {
            r ? e.track(t, n, r) : n ? e.track(t, n) : e.track(t)
        };
        return {
            identity: t,
            track: n
        }
    }),
    define("preloader", [], function() {
        var e = function() {
            var e = []
                , t = null
                , n = function() {
                for (var t = 0; t < e.length; t++)
                    e[t].end ? e.splice(t--, 1) : e[t]();
                !e.length && r()
            }
                , r = function() {
                clearInterval(t),
                    t = null
            };
            return function(r, i, s) {
                var o = {}, u, a, f, l, c = new Image;
                c.src = r;
                if (c.complete) {
                    i(c);
                    return
                }
                u = c.width,
                    a = c.height,
                    c.onerror = function() {
                        s && s(c),
                            o.end = !0,
                            c = c.onload = c.onerror = null
                    }
                ;
                var o = function() {
                    f = c.width,
                        l = c.height;
                    if (f !== u || l !== a || f * l > 1024)
                        i(c),
                            o.end = !0
                };
                o(),
                    c.onload = function() {
                        !o.end && o(),
                            c = c.onload = c.onerror = null
                    }
                    ,
                o.end || (e.push(o),
                t === null && (t = setInterval(n, 40)))
            }
        }();
        return {
            ready: e
        }
    }),
    define("common", ["require", "exports", "module", "avalon", "js-cookie", "statistics", "preloader"], function(e, t, n, r) {
        function u(e, t) {
            var n = "7899" + t;
            return e ^ parseInt(n, 10)
        }
        function a(e, t) {
            var n = e.toString()
                , r = t.toString()
                , i = n.length
                , s = n + r
                , o = "";
            for (var u = 0; u < s.length; u++)
                o += f() + s[u];
            return o += f() + i.toString(),
                o
        }
        function f() {
            return String.fromCharCode(97 + Math.floor(Math.random() * 100) % 26)
        }
        if (!window.console || typeof console.log == "undefined")
            window.console = {
                log: function() {}
            };
        $(function() {
            setTimeout(function() {
                $('<img width="0px" height="0px" style="display:none" />').appendTo("body").attr("src", "/images/small-logo.png")
            }, 100)
        }),
            $(document).delegate("#aLogin", "click", function() {
                var e = "/login";
                window.location.pathname == "/" ? r.track("登陆页", {
                    "来源页面": document.title
                }, function() {
                    window.location.href = e
                }) : r.track("登陆页", {
                    "来源页面": document.title
                }, function() {
                    window.location.href = e + "?returnURL=" + encodeURIComponent(window.location.href)
                })
            }),
            $(document).delegate("#aRegister", "click", function() {
                var e = "/register";
                window.location.pathname == "/" ? r.track("进入会员注册页", {
                    "页面来源": document.title
                }, function() {
                    window.location.href = e
                }) : r.track("进入会员注册页", {
                    "页面来源": document.title
                }, function() {
                    window.location.href = e + "?returnURL=" + encodeURIComponent(window.location.href)
                })
            });
        var i = e("avalon");
        i.config({
            debug: !1
        });
        var s = e("js-cookie")
            , r = e("statistics");
        !function(e) {
            function t(r) {
                if (n[r])
                    return n[r].exports;
                var i = n[r] = {
                    exports: {},
                    id: r,
                    loaded: !1
                };
                return e[r].call(i.exports, i, i.exports, t),
                    i.loaded = !0,
                    i.exports
            }
            var n = {};
            return t.m = e,
                t.c = n,
                t.p = "",
                t(0)
        }([function(e, t, n) {
            e.exports = n(1)
        }
            , function(e, t) {
                !function() {
                    var e;
                    if (window.console && "undefined" != typeof console.log) {
                        try {
                            (window.parent.__has_console_security_message || window.top.__has_console_security_message) && (e = !0)
                        } catch (t) {
                            e = !0
                        }
                        if (window.__has_console_security_message || e)
                            return;
                        var n = "一张网页，要经历怎样的过程，才能抵达用户面前？ 一位新人，要经历怎样的成长，才能站在技术之巅？\n探寻这里的秘密，体验这里的挑战，成为这里的主人！加入启信宝，你，可以影响世界。"
                            , r = "请将简历发送至  hr@bertadata.com（ 邮件标题请以“姓名-应聘XX职位-来自console”命名）"
                            , i = "职位介绍：http://x.eqxiu.com/s/X2hfzoI3?eqrcode=1";
                        /msie/gi.test(navigator.userAgent) ? (console.log(n),
                            console.log(r),
                            console.log(i)) : (console.log("%c 启信宝 %c Copyright © 2015-%s", 'font-family: "Helvetica Neue", Helvetica, Arial, sans-serif;font-size:32px;color:#ffc000;-webkit-text-fill-color:#ffc000;-webkit-text-stroke: 1px #ffc000;', "font-size:12px;color:#999999;", (new Date).getFullYear()),
                            console.log(n),
                            console.log(r),
                            console.log(i),
                            window.__has_console_security_message = !0)
                    }
                }()
            }
        ]);
        var o = e("preloader");
        $.ajaxSetup({
            global: !0,
            cache: !1
        }),
            $(function() {
                $('[title="站长统计"]').hide()
            }),
            $(document).ajaxSuccess(function(e, t, n) {
                var r = t.responseText;
                if (n.dataType == "json") {
                    var i = JSON.parse(r);
                    i.errcode == 999999 && (window.location = "/decline")
                }
            }),
            Date.prototype.Format = function(e) {
                var t = {
                    "M+": this.getMonth() + 1,
                    "d+": this.getDate(),
                    "h+": this.getHours(),
                    "m+": this.getMinutes(),
                    "s+": this.getSeconds(),
                    "q+": Math.floor((this.getMonth() + 3) / 3),
                    S: this.getMilliseconds()
                };
                /(y+)/.test(e) && (e = e.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length)));
                for (var n in t)
                    (new RegExp("(" + n + ")")).test(e) && (e = e.replace(RegExp.$1, RegExp.$1.length == 1 ? t[n] : ("00" + t[n]).substr(("" + t[n]).length)));
                return e
            }
            ,
            String.prototype.trim = function() {
                return this.replace(/(^\s*)|(\s*$)/g, "")
            }
            ,
            String.prototype.ltrim = function() {
                return this.replace(/(^\s*)/g, "")
            }
            ,
            String.prototype.rtrim = function() {
                return this.replace(/(\s*$)/g, "")
            }
            ,
            Array.prototype.remove = function(e) {
                if (isNaN(e) || e > this.length)
                    return !1;
                this.splice(e, 1)
            }
            ,
            t.Config = {
                ajaxBaseUrl: "/service/",
                thirdAPIBaseUrl: "http://localhost/ThirdAPI/api/",
                relationUrl: "/service/"
            },
            t.gtn = function() {
                var e = new Date
                    , t = e - new Date(e.getFullYear(),e.getMonth(),e.getDate())
                    , n = $("script[data-main]").data("main")
                    , r = n.substring(n.indexOf("?v=") + 3)
                    , i = u(t, r);
                return a(t, i)
            }
            ,
            t.Utility = function() {}
            ,
            t.Utility.prototype = {
                getAjaxCall: function(e, t) {
                    var n = $.Deferred();
                    return $.ajax({
                        type: "GET",
                        url: e,
                        data: t,
                        contentType: "application/json; charset=utf-8",
                        dataType: "json",
                        success: function(e) {
                            e == "lol" ? ($(document).trigger("login-required"),
                                n.reject(e)) : n.resolve(e)
                        }
                    }).fail(function(e) {
                        e.responseText == "lol" ? ($(document).trigger("login-required"),
                            n.reject()) : n.reject(e)
                    }),
                        n
                },
                postAjaxCall: function(e, t) {
                    var n = $.Deferred();
                    return $.ajax({
                        type: "POST",
                        url: e,
                        data: t,
                        success: function(e) {
                            e == "lol" ? ($(document).trigger("login-required"),
                                n.reject()) : n.resolve(e)
                        }
                    }),
                        n
                },
                queryStringByName: function(e) {
                    var t = location.href;
                    if (t.indexOf("?") > -1) {
                        var n = t.substring(t.indexOf("?") + 1)
                            , r = n.split("&");
                        for (var i = 0; i < r.length; i++) {
                            var s = r[i].split("=");
                            if (s[0] == e)
                                return s[1]
                        }
                    }
                    return ""
                },
                setCookie: function(e, t, n) {
                    if (n < 0) {
                        this.delCookie(e);
                        return
                    }
                    s.set(e, t, {
                        expires: n
                    })
                },
                getCookie: function(e) {
                    var t = s.get(e);
                    return t ? t : null
                },
                delCookie: function(e) {
                    s.remove(e)
                },
                formatDate: function(e) {
                    var t = new Date(e)
                        , n = new Date
                        , r = n.getTime() - t.getTime()
                        , i = Math.floor(r / 864e5)
                        , s = "";
                    if (i > 2)
                        return t.Format("yyyy-MM-dd");
                    var o = t.Format("hh:mm:ss");
                    switch (i) {
                        case 2:
                            s += "前天 " + o;
                            break;
                        case 1:
                            s += "昨天 " + o;
                            break;
                        default:
                            var u = Math.floor(r / 6e4)
                                , a = new Date(n.getFullYear(),n.getMonth(),n.getDate(),0,0,0)
                                , f = n.getTime() - a.getTime();
                            r > f ? s += "昨天" + o : u > 30 ? s += "今天 " + o : u <= 0 ? s += "刚刚" : s += u + "分钟前"
                    }
                    return s
                },
                preload: function(e) {
                    parentWidth = e.width(),
                        parentHeight = e.height();
                    var t = e.attr("pre-src")
                        , n = 0
                        , r = 0;
                    t && o.ready(t, function(t) {
                        t.width >= t.height ? (r = parentWidth / t.width * t.height,
                            n = parentWidth) : t.width < t.height && (n = parentHeight / t.height * t.width,
                            r = parentHeight),
                            e.append(t),
                            t.height = r == 0 ? parentHeight : r,
                            t.width = n == 0 ? parentWidth : n
                    })
                },
                windowopen: function(e, t, n, r) {
                    var i = (window.screen.availHeight - r) / 2
                        , s = (window.screen.availWidth - n) / 2
                        , o = [];
                    o.push("width=" + n),
                        o.push("height=" + r),
                        o.push("top=" + i),
                        o.push("left=" + s),
                        window.open(e, t, o.join(","))
                },
                browser: {
                    versions: function() {
                        var e = navigator.userAgent
                            , t = navigator.appVersion;
                        return {
                            trident: e.indexOf("Trident") > -1,
                            presto: e.indexOf("Presto") > -1,
                            webKit: e.indexOf("AppleWebKit") > -1,
                            gecko: e.indexOf("Gecko") > -1 && e.indexOf("KHTML") == -1,
                            mobile: !!e.match(/AppleWebKit.*Mobile.*/),
                            ios: !!e.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/),
                            android: e.indexOf("Android") > -1 || e.indexOf("Adr") > -1,
                            iPhone: e.indexOf("iPhone") > -1,
                            iPad: e.indexOf("iPad") > -1,
                            webApp: e.indexOf("Safari") == -1,
                            weixin: e.indexOf("MicroMessenger") > -1,
                            qq: e.match(/\sQQ/i) == " qq"
                        }
                    }(),
                    language: (navigator.browserLanguage || navigator.language).toLowerCase()
                },
                request: function(e) {
                    var t = location.href;
                    t = decodeURI(t);
                    var n = t.substring(t.indexOf("?") + 1, t.length).split("&")
                        , r = {};
                    for (var i = 0; j = n[i]; i++)
                        r[j.substring(0, j.indexOf("=")).toLowerCase()] = j.substring(j.indexOf("=") + 1, j.length);
                    var s = r[e.toLowerCase()];
                    return typeof s == "undefined" ? "" : s
                }
            }
    });
if (typeof jQuery == "undefined")
    throw new Error("Bootstrap's JavaScript requires jQuery");
+function(e) {
    var t = e.fn.jquery.split(" ")[0].split(".");
    if (t[0] < 2 && t[1] < 9 || t[0] == 1 && t[1] == 9 && t[2] < 1)
        throw new Error("Bootstrap's JavaScript requires jQuery version 1.9.1 or higher")
}(jQuery),
    +function(e) {
        function t() {
            var e = document.createElement("bootstrap")
                , t = {
                WebkitTransition: "webkitTransitionEnd",
                MozTransition: "transitionend",
                OTransition: "oTransitionEnd otransitionend",
                transition: "transitionend"
            };
            for (var n in t)
                if (e.style[n] !== undefined)
                    return {
                        end: t[n]
                    };
            return !1
        }
        e.fn.emulateTransitionEnd = function(t) {
            var n = !1
                , r = this;
            e(this).one("bsTransitionEnd", function() {
                n = !0
            });
            var i = function() {
                n || e(r).trigger(e.support.transition.end)
            };
            return setTimeout(i, t),
                this
        }
            ,
            e(function() {
                e.support.transition = t();
                if (!e.support.transition)
                    return;
                e.event.special.bsTransitionEnd = {
                    bindType: e.support.transition.end,
                    delegateType: e.support.transition.end,
                    handle: function(t) {
                        if (e(t.target).is(this))
                            return t.handleObj.handler.apply(this, arguments)
                    }
                }
            })
    }(jQuery),
    +function(e) {
        function r(t) {
            return this.each(function() {
                var r = e(this)
                    , i = r.data("bs.alert");
                i || r.data("bs.alert", i = new n(this)),
                typeof t == "string" && i[t].call(r)
            })
        }
        var t = '[data-dismiss="alert"]'
            , n = function(n) {
            e(n).on("click", t, this.close)
        };
        n.VERSION = "3.3.5",
            n.TRANSITION_DURATION = 150,
            n.prototype.close = function(t) {
                function o() {
                    s.detach().trigger("closed.bs.alert").remove()
                }
                var r = e(this)
                    , i = r.attr("data-target");
                i || (i = r.attr("href"),
                    i = i && i.replace(/.*(?=#[^\s]*$)/, ""));
                var s = e(i);
                t && t.preventDefault(),
                s.length || (s = r.closest(".alert")),
                    s.trigger(t = e.Event("close.bs.alert"));
                if (t.isDefaultPrevented())
                    return;
                s.removeClass("in"),
                    e.support.transition && s.hasClass("fade") ? s.one("bsTransitionEnd", o).emulateTransitionEnd(n.TRANSITION_DURATION) : o()
            }
        ;
        var i = e.fn.alert;
        e.fn.alert = r,
            e.fn.alert.Constructor = n,
            e.fn.alert.noConflict = function() {
                return e.fn.alert = i,
                    this
            }
            ,
            e(document).on("click.bs.alert.data-api", t, n.prototype.close)
    }(jQuery),
    +function(e) {
        function n(n) {
            return this.each(function() {
                var r = e(this)
                    , i = r.data("bs.button")
                    , s = typeof n == "object" && n;
                i || r.data("bs.button", i = new t(this,s)),
                    n == "toggle" ? i.toggle() : n && i.setState(n)
            })
        }
        var t = function(n, r) {
            this.$element = e(n),
                this.options = e.extend({}, t.DEFAULTS, r),
                this.isLoading = !1
        };
        t.VERSION = "3.3.5",
            t.DEFAULTS = {
                loadingText: "loading..."
            },
            t.prototype.setState = function(t) {
                var n = "disabled"
                    , r = this.$element
                    , i = r.is("input") ? "val" : "html"
                    , s = r.data();
                t += "Text",
                s.resetText == null && r.data("resetText", r[i]()),
                    setTimeout(e.proxy(function() {
                        r[i](s[t] == null ? this.options[t] : s[t]),
                            t == "loadingText" ? (this.isLoading = !0,
                                r.addClass(n).attr(n, n)) : this.isLoading && (this.isLoading = !1,
                                r.removeClass(n).removeAttr(n))
                    }, this), 0)
            }
            ,
            t.prototype.toggle = function() {
                var e = !0
                    , t = this.$element.closest('[data-toggle="buttons"]');
                if (t.length) {
                    var n = this.$element.find("input");
                    n.prop("type") == "radio" ? (n.prop("checked") && (e = !1),
                        t.find(".active").removeClass("active"),
                        this.$element.addClass("active")) : n.prop("type") == "checkbox" && (n.prop("checked") !== this.$element.hasClass("active") && (e = !1),
                        this.$element.toggleClass("active")),
                        n.prop("checked", this.$element.hasClass("active")),
                    e && n.trigger("change")
                } else
                    this.$element.attr("aria-pressed", !this.$element.hasClass("active")),
                        this.$element.toggleClass("active")
            }
        ;
        var r = e.fn.button;
        e.fn.button = n,
            e.fn.button.Constructor = t,
            e.fn.button.noConflict = function() {
                return e.fn.button = r,
                    this
            }
            ,
            e(document).on("click.bs.button.data-api", '[data-toggle^="button"]', function(t) {
                var r = e(t.target);
                r.hasClass("btn") || (r = r.closest(".btn")),
                    n.call(r, "toggle"),
                !e(t.target).is('input[type="radio"]') && !e(t.target).is('input[type="checkbox"]') && t.preventDefault()
            }).on("focus.bs.button.data-api blur.bs.button.data-api", '[data-toggle^="button"]', function(t) {
                e(t.target).closest(".btn").toggleClass("focus", /^focus(in)?$/.test(t.type))
            })
    }(jQuery),
    +function(e) {
        function n(n) {
            return this.each(function() {
                var r = e(this)
                    , i = r.data("bs.carousel")
                    , s = e.extend({}, t.DEFAULTS, r.data(), typeof n == "object" && n)
                    , o = typeof n == "string" ? n : s.slide;
                i || r.data("bs.carousel", i = new t(this,s)),
                    typeof n == "number" ? i.to(n) : o ? i[o]() : s.interval && i.pause().cycle()
            })
        }
        var t = function(t, n) {
            this.$element = e(t),
                this.$indicators = this.$element.find(".carousel-indicators"),
                this.options = n,
                this.paused = null,
                this.sliding = null,
                this.interval = null,
                this.$active = null,
                this.$items = null,
            this.options.keyboard && this.$element.on("keydown.bs.carousel", e.proxy(this.keydown, this)),
            this.options.pause == "hover" && !("ontouchstart"in document.documentElement) && this.$element.on("mouseenter.bs.carousel", e.proxy(this.pause, this)).on("mouseleave.bs.carousel", e.proxy(this.cycle, this))
        };
        t.VERSION = "3.3.5",
            t.TRANSITION_DURATION = 600,
            t.DEFAULTS = {
                interval: 5e3,
                pause: "hover",
                wrap: !0,
                keyboard: !0
            },
            t.prototype.keydown = function(e) {
                if (/input|textarea/i.test(e.target.tagName))
                    return;
                switch (e.which) {
                    case 37:
                        this.prev();
                        break;
                    case 39:
                        this.next();
                        break;
                    default:
                        return
                }
                e.preventDefault()
            }
            ,
            t.prototype.cycle = function(t) {
                return t || (this.paused = !1),
                this.interval && clearInterval(this.interval),
                this.options.interval && !this.paused && (this.interval = setInterval(e.proxy(this.next, this), this.options.interval)),
                    this
            }
            ,
            t.prototype.getItemIndex = function(e) {
                return this.$items = e.parent().children(".item"),
                    this.$items.index(e || this.$active)
            }
            ,
            t.prototype.getItemForDirection = function(e, t) {
                var n = this.getItemIndex(t)
                    , r = e == "prev" && n === 0 || e == "next" && n == this.$items.length - 1;
                if (r && !this.options.wrap)
                    return t;
                var i = e == "prev" ? -1 : 1
                    , s = (n + i) % this.$items.length;
                return this.$items.eq(s)
            }
            ,
            t.prototype.to = function(e) {
                var t = this
                    , n = this.getItemIndex(this.$active = this.$element.find(".item.active"));
                if (e > this.$items.length - 1 || e < 0)
                    return;
                return this.sliding ? this.$element.one("slid.bs.carousel", function() {
                    t.to(e)
                }) : n == e ? this.pause().cycle() : this.slide(e > n ? "next" : "prev", this.$items.eq(e))
            }
            ,
            t.prototype.pause = function(t) {
                return t || (this.paused = !0),
                this.$element.find(".next, .prev").length && e.support.transition && (this.$element.trigger(e.support.transition.end),
                    this.cycle(!0)),
                    this.interval = clearInterval(this.interval),
                    this
            }
            ,
            t.prototype.next = function() {
                if (this.sliding)
                    return;
                return this.slide("next")
            }
            ,
            t.prototype.prev = function() {
                if (this.sliding)
                    return;
                return this.slide("prev")
            }
            ,
            t.prototype.slide = function(n, r) {
                var i = this.$element.find(".item.active")
                    , s = r || this.getItemForDirection(n, i)
                    , o = this.interval
                    , u = n == "next" ? "left" : "right"
                    , a = this;
                if (s.hasClass("active"))
                    return this.sliding = !1;
                var f = s[0]
                    , l = e.Event("slide.bs.carousel", {
                    relatedTarget: f,
                    direction: u
                });
                this.$element.trigger(l);
                if (l.isDefaultPrevented())
                    return;
                this.sliding = !0,
                o && this.pause();
                if (this.$indicators.length) {
                    this.$indicators.find(".active").removeClass("active");
                    var c = e(this.$indicators.children()[this.getItemIndex(s)]);
                    c && c.addClass("active")
                }
                var h = e.Event("slid.bs.carousel", {
                    relatedTarget: f,
                    direction: u
                });
                return e.support.transition && this.$element.hasClass("slide") ? (s.addClass(n),
                    s[0].offsetWidth,
                    i.addClass(u),
                    s.addClass(u),
                    i.one("bsTransitionEnd", function() {
                        s.removeClass([n, u].join(" ")).addClass("active"),
                            i.removeClass(["active", u].join(" ")),
                            a.sliding = !1,
                            setTimeout(function() {
                                a.$element.trigger(h)
                            }, 0)
                    }).emulateTransitionEnd(t.TRANSITION_DURATION)) : (i.removeClass("active"),
                    s.addClass("active"),
                    this.sliding = !1,
                    this.$element.trigger(h)),
                o && this.cycle(),
                    this
            }
        ;
        var r = e.fn.carousel;
        e.fn.carousel = n,
            e.fn.carousel.Constructor = t,
            e.fn.carousel.noConflict = function() {
                return e.fn.carousel = r,
                    this
            }
        ;
        var i = function(t) {
            var r, i = e(this), s = e(i.attr("data-target") || (r = i.attr("href")) && r.replace(/.*(?=#[^\s]+$)/, ""));
            if (!s.hasClass("carousel"))
                return;
            var o = e.extend({}, s.data(), i.data())
                , u = i.attr("data-slide-to");
            u && (o.interval = !1),
                n.call(s, o),
            u && s.data("bs.carousel").to(u),
                t.preventDefault()
        };
        e(document).on("click.bs.carousel.data-api", "[data-slide]", i).on("click.bs.carousel.data-api", "[data-slide-to]", i),
            e(window).on("load", function() {
                e('[data-ride="carousel"]').each(function() {
                    var t = e(this);
                    n.call(t, t.data())
                })
            })
    }(jQuery),
    +function(e) {
        function n(t) {
            var n, r = t.attr("data-target") || (n = t.attr("href")) && n.replace(/.*(?=#[^\s]+$)/, "");
            return e(r)
        }
        function r(n) {
            return this.each(function() {
                var r = e(this)
                    , i = r.data("bs.collapse")
                    , s = e.extend({}, t.DEFAULTS, r.data(), typeof n == "object" && n);
                !i && s.toggle && /show|hide/.test(n) && (s.toggle = !1),
                i || r.data("bs.collapse", i = new t(this,s)),
                typeof n == "string" && i[n]()
            })
        }
        var t = function(n, r) {
            this.$element = e(n),
                this.options = e.extend({}, t.DEFAULTS, r),
                this.$trigger = e('[data-toggle="collapse"][href="#' + n.id + '"],' + '[data-toggle="collapse"][data-target="#' + n.id + '"]'),
                this.transitioning = null,
                this.options.parent ? this.$parent = this.getParent() : this.addAriaAndCollapsedClass(this.$element, this.$trigger),
            this.options.toggle && this.toggle()
        };
        t.VERSION = "3.3.5",
            t.TRANSITION_DURATION = 350,
            t.DEFAULTS = {
                toggle: !0
            },
            t.prototype.dimension = function() {
                var e = this.$element.hasClass("width");
                return e ? "width" : "height"
            }
            ,
            t.prototype.show = function() {
                if (this.transitioning || this.$element.hasClass("in"))
                    return;
                var n, i = this.$parent && this.$parent.children(".panel").children(".in, .collapsing");
                if (i && i.length) {
                    n = i.data("bs.collapse");
                    if (n && n.transitioning)
                        return
                }
                var s = e.Event("show.bs.collapse");
                this.$element.trigger(s);
                if (s.isDefaultPrevented())
                    return;
                i && i.length && (r.call(i, "hide"),
                n || i.data("bs.collapse", null));
                var o = this.dimension();
                this.$element.removeClass("collapse").addClass("collapsing")[o](0).attr("aria-expanded", !0),
                    this.$trigger.removeClass("collapsed").attr("aria-expanded", !0),
                    this.transitioning = 1;
                var u = function() {
                    this.$element.removeClass("collapsing").addClass("collapse in")[o](""),
                        this.transitioning = 0,
                        this.$element.trigger("shown.bs.collapse")
                };
                if (!e.support.transition)
                    return u.call(this);
                var a = e.camelCase(["scroll", o].join("-"));
                this.$element.one("bsTransitionEnd", e.proxy(u, this)).emulateTransitionEnd(t.TRANSITION_DURATION)[o](this.$element[0][a])
            }
            ,
            t.prototype.hide = function() {
                if (this.transitioning || !this.$element.hasClass("in"))
                    return;
                var n = e.Event("hide.bs.collapse");
                this.$element.trigger(n);
                if (n.isDefaultPrevented())
                    return;
                var r = this.dimension();
                this.$element[r](this.$element[r]())[0].offsetHeight,
                    this.$element.addClass("collapsing").removeClass("collapse in").attr("aria-expanded", !1),
                    this.$trigger.addClass("collapsed").attr("aria-expanded", !1),
                    this.transitioning = 1;
                var i = function() {
                    this.transitioning = 0,
                        this.$element.removeClass("collapsing").addClass("collapse").trigger("hidden.bs.collapse")
                };
                if (!e.support.transition)
                    return i.call(this);
                this.$element[r](0).one("bsTransitionEnd", e.proxy(i, this)).emulateTransitionEnd(t.TRANSITION_DURATION)
            }
            ,
            t.prototype.toggle = function() {
                this[this.$element.hasClass("in") ? "hide" : "show"]()
            }
            ,
            t.prototype.getParent = function() {
                return e(this.options.parent).find('[data-toggle="collapse"][data-parent="' + this.options.parent + '"]').each(e.proxy(function(t, r) {
                    var i = e(r);
                    this.addAriaAndCollapsedClass(n(i), i)
                }, this)).end()
            }
            ,
            t.prototype.addAriaAndCollapsedClass = function(e, t) {
                var n = e.hasClass("in");
                e.attr("aria-expanded", n),
                    t.toggleClass("collapsed", !n).attr("aria-expanded", n)
            }
        ;
        var i = e.fn.collapse;
        e.fn.collapse = r,
            e.fn.collapse.Constructor = t,
            e.fn.collapse.noConflict = function() {
                return e.fn.collapse = i,
                    this
            }
            ,
            e(document).on("click.bs.collapse.data-api", '[data-toggle="collapse"]', function(t) {
                var i = e(this);
                i.attr("data-target") || t.preventDefault();
                var s = n(i)
                    , o = s.data("bs.collapse")
                    , u = o ? "toggle" : i.data();
                r.call(s, u)
            })
    }(jQuery),
    +function(e) {
        function i(t) {
            var n = t.attr("data-target");
            n || (n = t.attr("href"),
                n = n && /#[A-Za-z]/.test(n) && n.replace(/.*(?=#[^\s]*$)/, ""));
            var r = n && e(n);
            return r && r.length ? r : t.parent()
        }
        function s(r) {
            if (r && r.which === 3)
                return;
            e(t).remove(),
                e(n).each(function() {
                    var t = e(this)
                        , n = i(t)
                        , s = {
                        relatedTarget: this
                    };
                    if (!n.hasClass("open"))
                        return;
                    if (r && r.type == "click" && /input|textarea/i.test(r.target.tagName) && e.contains(n[0], r.target))
                        return;
                    n.trigger(r = e.Event("hide.bs.dropdown", s));
                    if (r.isDefaultPrevented())
                        return;
                    t.attr("aria-expanded", "false"),
                        n.removeClass("open").trigger("hidden.bs.dropdown", s)
                })
        }
        function o(t) {
            return this.each(function() {
                var n = e(this)
                    , i = n.data("bs.dropdown");
                i || n.data("bs.dropdown", i = new r(this)),
                typeof t == "string" && i[t].call(n)
            })
        }
        var t = ".dropdown-backdrop"
            , n = '[data-toggle="dropdown"]'
            , r = function(t) {
            e(t).on("click.bs.dropdown", this.toggle)
        };
        r.VERSION = "3.3.5",
            r.prototype.toggle = function(t) {
                var n = e(this);
                if (n.is(".disabled, :disabled"))
                    return;
                var r = i(n)
                    , o = r.hasClass("open");
                s();
                if (!o) {
                    "ontouchstart"in document.documentElement && !r.closest(".navbar-nav").length && e(document.createElement("div")).addClass("dropdown-backdrop").insertAfter(e(this)).on("click", s);
                    var u = {
                        relatedTarget: this
                    };
                    r.trigger(t = e.Event("show.bs.dropdown", u));
                    if (t.isDefaultPrevented())
                        return;
                    n.trigger("focus").attr("aria-expanded", "true"),
                        r.toggleClass("open").trigger("shown.bs.dropdown", u)
                }
                return !1
            }
            ,
            r.prototype.keydown = function(t) {
                if (!/(38|40|27|32)/.test(t.which) || /input|textarea/i.test(t.target.tagName))
                    return;
                var r = e(this);
                t.preventDefault(),
                    t.stopPropagation();
                if (r.is(".disabled, :disabled"))
                    return;
                var s = i(r)
                    , o = s.hasClass("open");
                if (!o && t.which != 27 || o && t.which == 27)
                    return t.which == 27 && s.find(n).trigger("focus"),
                        r.trigger("click");
                var u = " li:not(.disabled):visible a"
                    , a = s.find(".dropdown-menu" + u);
                if (!a.length)
                    return;
                var f = a.index(t.target);
                t.which == 38 && f > 0 && f--,
                t.which == 40 && f < a.length - 1 && f++,
                ~f || (f = 0),
                    a.eq(f).trigger("focus")
            }
        ;
        var u = e.fn.dropdown;
        e.fn.dropdown = o,
            e.fn.dropdown.Constructor = r,
            e.fn.dropdown.noConflict = function() {
                return e.fn.dropdown = u,
                    this
            }
            ,
            e(document).on("click.bs.dropdown.data-api", s).on("click.bs.dropdown.data-api", ".dropdown form", function(e) {
                e.stopPropagation()
            }).on("click.bs.dropdown.data-api", n, r.prototype.toggle).on("keydown.bs.dropdown.data-api", n, r.prototype.keydown).on("keydown.bs.dropdown.data-api", ".dropdown-menu", r.prototype.keydown)
    }(jQuery),
    +function(e) {
        function n(n, r) {
            return this.each(function() {
                var i = e(this)
                    , s = i.data("bs.modal")
                    , o = e.extend({}, t.DEFAULTS, i.data(), typeof n == "object" && n);
                s || i.data("bs.modal", s = new t(this,o)),
                    typeof n == "string" ? s[n](r) : o.show && s.show(r)
            })
        }
        var t = function(t, n) {
            this.options = n,
                this.$body = e(document.body),
                this.$element = e(t),
                this.$dialog = this.$element.find(".modal-dialog"),
                this.$backdrop = null,
                this.isShown = null,
                this.originalBodyPad = null,
                this.scrollbarWidth = 0,
                this.ignoreBackdropClick = !1,
            this.options.remote && this.$element.find(".modal-content").load(this.options.remote, e.proxy(function() {
                this.$element.trigger("loaded.bs.modal")
            }, this))
        };
        t.VERSION = "3.3.5",
            t.TRANSITION_DURATION = 300,
            t.BACKDROP_TRANSITION_DURATION = 150,
            t.DEFAULTS = {
                backdrop: !0,
                keyboard: !0,
                show: !0
            },
            t.prototype.toggle = function(e) {
                return this.isShown ? this.hide() : this.show(e)
            }
            ,
            t.prototype.show = function(n) {
                var r = this
                    , i = e.Event("show.bs.modal", {
                    relatedTarget: n
                });
                this.$element.trigger(i);
                if (this.isShown || i.isDefaultPrevented())
                    return;
                this.isShown = !0,
                    this.checkScrollbar(),
                    this.setScrollbar(),
                    this.$body.addClass("modal-open"),
                    this.escape(),
                    this.resize(),
                    this.$element.on("click.dismiss.bs.modal", '[data-dismiss="modal"]', e.proxy(this.hide, this)),
                    this.$dialog.on("mousedown.dismiss.bs.modal", function() {
                        r.$element.one("mouseup.dismiss.bs.modal", function(t) {
                            e(t.target).is(r.$element) && (r.ignoreBackdropClick = !0)
                        })
                    }),
                    this.backdrop(function() {
                        var i = e.support.transition && r.$element.hasClass("fade");
                        r.$element.parent().length || r.$element.appendTo(r.$body),
                            r.$element.show().scrollTop(0),
                            r.adjustDialog(),
                        i && r.$element[0].offsetWidth,
                            r.$element.addClass("in"),
                            r.enforceFocus();
                        var s = e.Event("shown.bs.modal", {
                            relatedTarget: n
                        });
                        i ? r.$dialog.one("bsTransitionEnd", function() {
                            r.$element.trigger("focus").trigger(s)
                        }).emulateTransitionEnd(t.TRANSITION_DURATION) : r.$element.trigger("focus").trigger(s)
                    })
            }
            ,
            t.prototype.hide = function(n) {
                n && n.preventDefault(),
                    n = e.Event("hide.bs.modal"),
                    this.$element.trigger(n);
                if (!this.isShown || n.isDefaultPrevented())
                    return;
                this.isShown = !1,
                    this.escape(),
                    this.resize(),
                    e(document).off("focusin.bs.modal"),
                    this.$element.removeClass("in").off("click.dismiss.bs.modal").off("mouseup.dismiss.bs.modal"),
                    this.$dialog.off("mousedown.dismiss.bs.modal"),
                    e.support.transition && this.$element.hasClass("fade") ? this.$element.one("bsTransitionEnd", e.proxy(this.hideModal, this)).emulateTransitionEnd(t.TRANSITION_DURATION) : this.hideModal()
            }
            ,
            t.prototype.enforceFocus = function() {
                e(document).off("focusin.bs.modal").on("focusin.bs.modal", e.proxy(function(e) {
                    this.$element[0] !== e.target && !this.$element.has(e.target).length && this.$element.trigger("focus")
                }, this))
            }
            ,
            t.prototype.escape = function() {
                this.isShown && this.options.keyboard ? this.$element.on("keydown.dismiss.bs.modal", e.proxy(function(e) {
                    e.which == 27 && this.hide()
                }, this)) : this.isShown || this.$element.off("keydown.dismiss.bs.modal")
            }
            ,
            t.prototype.resize = function() {
                this.isShown ? e(window).on("resize.bs.modal", e.proxy(this.handleUpdate, this)) : e(window).off("resize.bs.modal")
            }
            ,
            t.prototype.hideModal = function() {
                var e = this;
                this.$element.hide(),
                    this.backdrop(function() {
                        e.$body.removeClass("modal-open"),
                            e.resetAdjustments(),
                            e.resetScrollbar(),
                            e.$element.trigger("hidden.bs.modal")
                    })
            }
            ,
            t.prototype.removeBackdrop = function() {
                this.$backdrop && this.$backdrop.remove(),
                    this.$backdrop = null
            }
            ,
            t.prototype.backdrop = function(n) {
                var r = this
                    , i = this.$element.hasClass("fade") ? "fade" : "";
                if (this.isShown && this.options.backdrop) {
                    var s = e.support.transition && i;
                    this.$backdrop = e(document.createElement("div")).addClass("modal-backdrop " + i).appendTo(this.$body),
                        this.$element.on("click.dismiss.bs.modal", e.proxy(function(e) {
                            if (this.ignoreBackdropClick) {
                                this.ignoreBackdropClick = !1;
                                return
                            }
                            if (e.target !== e.currentTarget)
                                return;
                            this.options.backdrop == "static" ? this.$element[0].focus() : this.hide()
                        }, this)),
                    s && this.$backdrop[0].offsetWidth,
                        this.$backdrop.addClass("in");
                    if (!n)
                        return;
                    s ? this.$backdrop.one("bsTransitionEnd", n).emulateTransitionEnd(t.BACKDROP_TRANSITION_DURATION) : n()
                } else if (!this.isShown && this.$backdrop) {
                    this.$backdrop.removeClass("in");
                    var o = function() {
                        r.removeBackdrop(),
                        n && n()
                    };
                    e.support.transition && this.$element.hasClass("fade") ? this.$backdrop.one("bsTransitionEnd", o).emulateTransitionEnd(t.BACKDROP_TRANSITION_DURATION) : o()
                } else
                    n && n()
            }
            ,
            t.prototype.handleUpdate = function() {
                this.adjustDialog()
            }
            ,
            t.prototype.adjustDialog = function() {
                var e = this.$element[0].scrollHeight > document.documentElement.clientHeight;
                this.$element.css({
                    paddingLeft: !this.bodyIsOverflowing && e ? this.scrollbarWidth : "",
                    paddingRight: this.bodyIsOverflowing && !e ? this.scrollbarWidth : ""
                })
            }
            ,
            t.prototype.resetAdjustments = function() {
                this.$element.css({
                    paddingLeft: "",
                    paddingRight: ""
                })
            }
            ,
            t.prototype.checkScrollbar = function() {
                var e = window.innerWidth;
                if (!e) {
                    var t = document.documentElement.getBoundingClientRect();
                    e = t.right - Math.abs(t.left)
                }
                this.bodyIsOverflowing = document.body.clientWidth < e,
                    this.scrollbarWidth = this.measureScrollbar()
            }
            ,
            t.prototype.setScrollbar = function() {
                var e = parseInt(this.$body.css("padding-right") || 0, 10);
                this.originalBodyPad = document.body.style.paddingRight || "",
                this.bodyIsOverflowing && this.$body.css("padding-right", e + this.scrollbarWidth)
            }
            ,
            t.prototype.resetScrollbar = function() {
                this.$body.css("padding-right", this.originalBodyPad)
            }
            ,
            t.prototype.measureScrollbar = function() {
                var e = document.createElement("div");
                e.className = "modal-scrollbar-measure",
                    this.$body.append(e);
                var t = e.offsetWidth - e.clientWidth;
                return this.$body[0].removeChild(e),
                    t
            }
        ;
        var r = e.fn.modal;
        e.fn.modal = n,
            e.fn.modal.Constructor = t,
            e.fn.modal.noConflict = function() {
                return e.fn.modal = r,
                    this
            }
            ,
            e(document).on("click.bs.modal.data-api", '[data-toggle="modal"]', function(t) {
                var r = e(this)
                    , i = r.attr("href")
                    , s = e(r.attr("data-target") || i && i.replace(/.*(?=#[^\s]+$)/, ""))
                    , o = s.data("bs.modal") ? "toggle" : e.extend({
                    remote: !/#/.test(i) && i
                }, s.data(), r.data());
                r.is("a") && t.preventDefault(),
                    s.one("show.bs.modal", function(e) {
                        if (e.isDefaultPrevented())
                            return;
                        s.one("hidden.bs.modal", function() {
                            r.is(":visible") && r.trigger("focus")
                        })
                    }),
                    n.call(s, o, this)
            })
    }(jQuery),
    +function(e) {
        function n(n) {
            return this.each(function() {
                var r = e(this)
                    , i = r.data("bs.tooltip")
                    , s = typeof n == "object" && n;
                if (!i && /destroy|hide/.test(n))
                    return;
                i || r.data("bs.tooltip", i = new t(this,s)),
                typeof n == "string" && i[n]()
            })
        }
        var t = function(e, t) {
            this.type = null,
                this.options = null,
                this.enabled = null,
                this.timeout = null,
                this.hoverState = null,
                this.$element = null,
                this.inState = null,
                this.init("tooltip", e, t)
        };
        t.VERSION = "3.3.5",
            t.TRANSITION_DURATION = 150,
            t.DEFAULTS = {
                animation: !0,
                placement: "top",
                selector: !1,
                template: '<div class="tooltip" role="tooltip"><div class="tooltip-arrow"></div><div class="tooltip-inner"></div></div>',
                trigger: "hover focus",
                title: "",
                delay: 0,
                html: !1,
                container: !1,
                viewport: {
                    selector: "body",
                    padding: 0
                }
            },
            t.prototype.init = function(t, n, r) {
                this.enabled = !0,
                    this.type = t,
                    this.$element = e(n),
                    this.options = this.getOptions(r),
                    this.$viewport = this.options.viewport && e(e.isFunction(this.options.viewport) ? this.options.viewport.call(this, this.$element) : this.options.viewport.selector || this.options.viewport),
                    this.inState = {
                        click: !1,
                        hover: !1,
                        focus: !1
                    };
                if (this.$element[0]instanceof document.constructor && !this.options.selector)
                    throw new Error("`selector` option must be specified when initializing " + this.type + " on the window.document object!");
                var i = this.options.trigger.split(" ");
                for (var s = i.length; s--; ) {
                    var o = i[s];
                    if (o == "click")
                        this.$element.on("click." + this.type, this.options.selector, e.proxy(this.toggle, this));
                    else if (o != "manual") {
                        var u = o == "hover" ? "mouseenter" : "focusin"
                            , a = o == "hover" ? "mouseleave" : "focusout";
                        this.$element.on(u + "." + this.type, this.options.selector, e.proxy(this.enter, this)),
                            this.$element.on(a + "." + this.type, this.options.selector, e.proxy(this.leave, this))
                    }
                }
                this.options.selector ? this._options = e.extend({}, this.options, {
                    trigger: "manual",
                    selector: ""
                }) : this.fixTitle()
            }
            ,
            t.prototype.getDefaults = function() {
                return t.DEFAULTS
            }
            ,
            t.prototype.getOptions = function(t) {
                return t = e.extend({}, this.getDefaults(), this.$element.data(), t),
                t.delay && typeof t.delay == "number" && (t.delay = {
                    show: t.delay,
                    hide: t.delay
                }),
                    t
            }
            ,
            t.prototype.getDelegateOptions = function() {
                var t = {}
                    , n = this.getDefaults();
                return this._options && e.each(this._options, function(e, r) {
                    n[e] != r && (t[e] = r)
                }),
                    t
            }
            ,
            t.prototype.enter = function(t) {
                var n = t instanceof this.constructor ? t : e(t.currentTarget).data("bs." + this.type);
                n || (n = new this.constructor(t.currentTarget,this.getDelegateOptions()),
                    e(t.currentTarget).data("bs." + this.type, n)),
                t instanceof e.Event && (n.inState[t.type == "focusin" ? "focus" : "hover"] = !0);
                if (n.tip().hasClass("in") || n.hoverState == "in") {
                    n.hoverState = "in";
                    return
                }
                clearTimeout(n.timeout),
                    n.hoverState = "in";
                if (!n.options.delay || !n.options.delay.show)
                    return n.show();
                n.timeout = setTimeout(function() {
                    n.hoverState == "in" && n.show()
                }, n.options.delay.show)
            }
            ,
            t.prototype.isInStateTrue = function() {
                for (var e in this.inState)
                    if (this.inState[e])
                        return !0;
                return !1
            }
            ,
            t.prototype.leave = function(t) {
                var n = t instanceof this.constructor ? t : e(t.currentTarget).data("bs." + this.type);
                n || (n = new this.constructor(t.currentTarget,this.getDelegateOptions()),
                    e(t.currentTarget).data("bs." + this.type, n)),
                t instanceof e.Event && (n.inState[t.type == "focusout" ? "focus" : "hover"] = !1);
                if (n.isInStateTrue())
                    return;
                clearTimeout(n.timeout),
                    n.hoverState = "out";
                if (!n.options.delay || !n.options.delay.hide)
                    return n.hide();
                n.timeout = setTimeout(function() {
                    n.hoverState == "out" && n.hide()
                }, n.options.delay.hide)
            }
            ,
            t.prototype.show = function() {
                var n = e.Event("show.bs." + this.type);
                if (this.hasContent() && this.enabled) {
                    this.$element.trigger(n);
                    var r = e.contains(this.$element[0].ownerDocument.documentElement, this.$element[0]);
                    if (n.isDefaultPrevented() || !r)
                        return;
                    var i = this
                        , s = this.tip()
                        , o = this.getUID(this.type);
                    this.setContent(),
                        s.attr("id", o),
                        this.$element.attr("aria-describedby", o),
                    this.options.animation && s.addClass("fade");
                    var u = typeof this.options.placement == "function" ? this.options.placement.call(this, s[0], this.$element[0]) : this.options.placement
                        , a = /\s?auto?\s?/i
                        , f = a.test(u);
                    f && (u = u.replace(a, "") || "top"),
                        s.detach().css({
                            top: 0,
                            left: 0,
                            display: "block"
                        }).addClass(u).data("bs." + this.type, this),
                        this.options.container ? s.appendTo(this.options.container) : s.insertAfter(this.$element),
                        this.$element.trigger("inserted.bs." + this.type);
                    var l = this.getPosition()
                        , c = s[0].offsetWidth
                        , h = s[0].offsetHeight;
                    if (f) {
                        var p = u
                            , d = this.getPosition(this.$viewport);
                        u = u == "bottom" && l.bottom + h > d.bottom ? "top" : u == "top" && l.top - h < d.top ? "bottom" : u == "right" && l.right + c > d.width ? "left" : u == "left" && l.left - c < d.left ? "right" : u,
                            s.removeClass(p).addClass(u)
                    }
                    var v = this.getCalculatedOffset(u, l, c, h);
                    this.applyPlacement(v, u);
                    var m = function() {
                        var e = i.hoverState;
                        i.$element.trigger("shown.bs." + i.type),
                            i.hoverState = null,
                        e == "out" && i.leave(i)
                    };
                    e.support.transition && this.$tip.hasClass("fade") ? s.one("bsTransitionEnd", m).emulateTransitionEnd(t.TRANSITION_DURATION) : m()
                }
            }
            ,
            t.prototype.applyPlacement = function(t, n) {
                var r = this.tip()
                    , i = r[0].offsetWidth
                    , s = r[0].offsetHeight
                    , o = parseInt(r.css("margin-top"), 10)
                    , u = parseInt(r.css("margin-left"), 10);
                isNaN(o) && (o = 0),
                isNaN(u) && (u = 0),
                    t.top += o,
                    t.left += u,
                    e.offset.setOffset(r[0], e.extend({
                        using: function(e) {
                            r.css({
                                top: Math.round(e.top),
                                left: Math.round(e.left)
                            })
                        }
                    }, t), 0),
                    r.addClass("in");
                var a = r[0].offsetWidth
                    , f = r[0].offsetHeight;
                n == "top" && f != s && (t.top = t.top + s - f);
                var l = this.getViewportAdjustedDelta(n, t, a, f);
                l.left ? t.left += l.left : t.top += l.top;
                var c = /top|bottom/.test(n)
                    , h = c ? l.left * 2 - i + a : l.top * 2 - s + f
                    , p = c ? "offsetWidth" : "offsetHeight";
                r.offset(t),
                    this.replaceArrow(h, r[0][p], c)
            }
            ,
            t.prototype.replaceArrow = function(e, t, n) {
                this.arrow().css(n ? "left" : "top", 50 * (1 - e / t) + "%").css(n ? "top" : "left", "")
            }
            ,
            t.prototype.setContent = function() {
                var e = this.tip()
                    , t = this.getTitle();
                e.find(".tooltip-inner")[this.options.html ? "html" : "text"](t),
                    e.removeClass("fade in top bottom left right")
            }
            ,
            t.prototype.hide = function(n) {
                function o() {
                    r.hoverState != "in" && i.detach(),
                        r.$element.removeAttr("aria-describedby").trigger("hidden.bs." + r.type),
                    n && n()
                }
                var r = this
                    , i = e(this.$tip)
                    , s = e.Event("hide.bs." + this.type);
                this.$element.trigger(s);
                if (s.isDefaultPrevented())
                    return;
                return i.removeClass("in"),
                    e.support.transition && i.hasClass("fade") ? i.one("bsTransitionEnd", o).emulateTransitionEnd(t.TRANSITION_DURATION) : o(),
                    this.hoverState = null,
                    this
            }
            ,
            t.prototype.fixTitle = function() {
                var e = this.$element;
                (e.attr("title") || typeof e.attr("data-original-title") != "string") && e.attr("data-original-title", e.attr("title") || "").attr("title", "")
            }
            ,
            t.prototype.hasContent = function() {
                return this.getTitle()
            }
            ,
            t.prototype.getPosition = function(t) {
                t = t || this.$element;
                var n = t[0]
                    , r = n.tagName == "BODY"
                    , i = n.getBoundingClientRect();
                i.width == null && (i = e.extend({}, i, {
                    width: i.right - i.left,
                    height: i.bottom - i.top
                }));
                var s = r ? {
                    top: 0,
                    left: 0
                } : t.offset()
                    , o = {
                    scroll: r ? document.documentElement.scrollTop || document.body.scrollTop : t.scrollTop()
                }
                    , u = r ? {
                    width: e(window).width(),
                    height: e(window).height()
                } : null;
                return e.extend({}, i, o, u, s)
            }
            ,
            t.prototype.getCalculatedOffset = function(e, t, n, r) {
                return e == "bottom" ? {
                    top: t.top + t.height,
                    left: t.left + t.width / 2 - n / 2
                } : e == "top" ? {
                    top: t.top - r,
                    left: t.left + t.width / 2 - n / 2
                } : e == "left" ? {
                    top: t.top + t.height / 2 - r / 2,
                    left: t.left - n
                } : {
                    top: t.top + t.height / 2 - r / 2,
                    left: t.left + t.width
                }
            }
            ,
            t.prototype.getViewportAdjustedDelta = function(e, t, n, r) {
                var i = {
                    top: 0,
                    left: 0
                };
                if (!this.$viewport)
                    return i;
                var s = this.options.viewport && this.options.viewport.padding || 0
                    , o = this.getPosition(this.$viewport);
                if (/right|left/.test(e)) {
                    var u = t.top - s - o.scroll
                        , a = t.top + s - o.scroll + r;
                    u < o.top ? i.top = o.top - u : a > o.top + o.height && (i.top = o.top + o.height - a)
                } else {
                    var f = t.left - s
                        , l = t.left + s + n;
                    f < o.left ? i.left = o.left - f : l > o.right && (i.left = o.left + o.width - l)
                }
                return i
            }
            ,
            t.prototype.getTitle = function() {
                var e, t = this.$element, n = this.options;
                return e = t.attr("data-original-title") || (typeof n.title == "function" ? n.title.call(t[0]) : n.title),
                    e
            }
            ,
            t.prototype.getUID = function(e) {
                do
                    e += ~~(Math.random() * 1e6);
                while (document.getElementById(e));return e
            }
            ,
            t.prototype.tip = function() {
                if (!this.$tip) {
                    this.$tip = e(this.options.template);
                    if (this.$tip.length != 1)
                        throw new Error(this.type + " `template` option must consist of exactly 1 top-level element!")
                }
                return this.$tip
            }
            ,
            t.prototype.arrow = function() {
                return this.$arrow = this.$arrow || this.tip().find(".tooltip-arrow")
            }
            ,
            t.prototype.enable = function() {
                this.enabled = !0
            }
            ,
            t.prototype.disable = function() {
                this.enabled = !1
            }
            ,
            t.prototype.toggleEnabled = function() {
                this.enabled = !this.enabled
            }
            ,
            t.prototype.toggle = function(t) {
                var n = this;
                t && (n = e(t.currentTarget).data("bs." + this.type),
                n || (n = new this.constructor(t.currentTarget,this.getDelegateOptions()),
                    e(t.currentTarget).data("bs." + this.type, n))),
                    t ? (n.inState.click = !n.inState.click,
                        n.isInStateTrue() ? n.enter(n) : n.leave(n)) : n.tip().hasClass("in") ? n.leave(n) : n.enter(n)
            }
            ,
            t.prototype.destroy = function() {
                var e = this;
                clearTimeout(this.timeout),
                    this.hide(function() {
                        e.$element.off("." + e.type).removeData("bs." + e.type),
                        e.$tip && e.$tip.detach(),
                            e.$tip = null,
                            e.$arrow = null,
                            e.$viewport = null
                    })
            }
        ;
        var r = e.fn.tooltip;
        e.fn.tooltip = n,
            e.fn.tooltip.Constructor = t,
            e.fn.tooltip.noConflict = function() {
                return e.fn.tooltip = r,
                    this
            }
    }(jQuery),
    +function(e) {
        function n(n) {
            return this.each(function() {
                var r = e(this)
                    , i = r.data("bs.popover")
                    , s = typeof n == "object" && n;
                if (!i && /destroy|hide/.test(n))
                    return;
                i || r.data("bs.popover", i = new t(this,s)),
                typeof n == "string" && i[n]()
            })
        }
        var t = function(e, t) {
            this.init("popover", e, t)
        };
        if (!e.fn.tooltip)
            throw new Error("Popover requires tooltip.js");
        t.VERSION = "3.3.5",
            t.DEFAULTS = e.extend({}, e.fn.tooltip.Constructor.DEFAULTS, {
                placement: "right",
                trigger: "click",
                content: "",
                template: '<div class="popover" role="tooltip"><div class="arrow"></div><h3 class="popover-title"></h3><div class="popover-content"></div></div>'
            }),
            t.prototype = e.extend({}, e.fn.tooltip.Constructor.prototype),
            t.prototype.constructor = t,
            t.prototype.getDefaults = function() {
                return t.DEFAULTS
            }
            ,
            t.prototype.setContent = function() {
                var e = this.tip()
                    , t = this.getTitle()
                    , n = this.getContent();
                e.find(".popover-title")[this.options.html ? "html" : "text"](t),
                    e.find(".popover-content").children().detach().end()[this.options.html ? typeof n == "string" ? "html" : "append" : "text"](n),
                    e.removeClass("fade top bottom left right in"),
                e.find(".popover-title").html() || e.find(".popover-title").hide()
            }
            ,
            t.prototype.hasContent = function() {
                return this.getTitle() || this.getContent()
            }
            ,
            t.prototype.getContent = function() {
                var e = this.$element
                    , t = this.options;
                return e.attr("data-content") || (typeof t.content == "function" ? t.content.call(e[0]) : t.content)
            }
            ,
            t.prototype.arrow = function() {
                return this.$arrow = this.$arrow || this.tip().find(".arrow")
            }
        ;
        var r = e.fn.popover;
        e.fn.popover = n,
            e.fn.popover.Constructor = t,
            e.fn.popover.noConflict = function() {
                return e.fn.popover = r,
                    this
            }
    }(jQuery),
    +function(e) {
        function t(n, r) {
            this.$body = e(document.body),
                this.$scrollElement = e(n).is(document.body) ? e(window) : e(n),
                this.options = e.extend({}, t.DEFAULTS, r),
                this.selector = (this.options.target || "") + " .nav li > a",
                this.offsets = [],
                this.targets = [],
                this.activeTarget = null,
                this.scrollHeight = 0,
                this.$scrollElement.on("scroll.bs.scrollspy", e.proxy(this.process, this)),
                this.refresh(),
                this.process()
        }
        function n(n) {
            return this.each(function() {
                var r = e(this)
                    , i = r.data("bs.scrollspy")
                    , s = typeof n == "object" && n;
                i || r.data("bs.scrollspy", i = new t(this,s)),
                typeof n == "string" && i[n]()
            })
        }
        t.VERSION = "3.3.5",
            t.DEFAULTS = {
                offset: 10
            },
            t.prototype.getScrollHeight = function() {
                return this.$scrollElement[0].scrollHeight || Math.max(this.$body[0].scrollHeight, document.documentElement.scrollHeight)
            }
            ,
            t.prototype.refresh = function() {
                var t = this
                    , n = "offset"
                    , r = 0;
                this.offsets = [],
                    this.targets = [],
                    this.scrollHeight = this.getScrollHeight(),
                e.isWindow(this.$scrollElement[0]) || (n = "position",
                    r = this.$scrollElement.scrollTop()),
                    this.$body.find(this.selector).map(function() {
                        var t = e(this)
                            , i = t.data("target") || t.attr("href")
                            , s = /^#./.test(i) && e(i);
                        return s && s.length && s.is(":visible") && [[s[n]().top + r, i]] || null
                    }).sort(function(e, t) {
                        return e[0] - t[0]
                    }).each(function() {
                        t.offsets.push(this[0]),
                            t.targets.push(this[1])
                    })
            }
            ,
            t.prototype.process = function() {
                var e = this.$scrollElement.scrollTop() + this.options.offset, t = this.getScrollHeight(), n = this.options.offset + t - this.$scrollElement.height(), r = this.offsets, i = this.targets, s = this.activeTarget, o;
                this.scrollHeight != t && this.refresh();
                if (e >= n)
                    return s != (o = i[i.length - 1]) && this.activate(o);
                if (s && e < r[0])
                    return this.activeTarget = null,
                        this.clear();
                for (o = r.length; o--; )
                    s != i[o] && e >= r[o] && (r[o + 1] === undefined || e < r[o + 1]) && this.activate(i[o])
            }
            ,
            t.prototype.activate = function(t) {
                this.activeTarget = t,
                    this.clear();
                var n = this.selector + '[data-target="' + t + '"],' + this.selector + '[href="' + t + '"]'
                    , r = e(n).parents("li").addClass("active");
                r.parent(".dropdown-menu").length && (r = r.closest("li.dropdown").addClass("active")),
                    r.trigger("activate.bs.scrollspy")
            }
            ,
            t.prototype.clear = function() {
                e(this.selector).parentsUntil(this.options.target, ".active").removeClass("active")
            }
        ;
        var r = e.fn.scrollspy;
        e.fn.scrollspy = n,
            e.fn.scrollspy.Constructor = t,
            e.fn.scrollspy.noConflict = function() {
                return e.fn.scrollspy = r,
                    this
            }
            ,
            e(window).on("load.bs.scrollspy.data-api", function() {
                e('[data-spy="scroll"]').each(function() {
                    var t = e(this);
                    n.call(t, t.data())
                })
            })
    }(jQuery),
    +function(e) {
        function n(n) {
            return this.each(function() {
                var r = e(this)
                    , i = r.data("bs.tab");
                i || r.data("bs.tab", i = new t(this)),
                typeof n == "string" && i[n]()
            })
        }
        var t = function(t) {
            this.element = e(t)
        };
        t.VERSION = "3.3.5",
            t.TRANSITION_DURATION = 150,
            t.prototype.show = function() {
                var t = this.element
                    , n = t.closest("ul:not(.dropdown-menu)")
                    , r = t.data("target");
                r || (r = t.attr("href"),
                    r = r && r.replace(/.*(?=#[^\s]*$)/, ""));
                if (t.parent("li").hasClass("active"))
                    return;
                var i = n.find(".active:last a")
                    , s = e.Event("hide.bs.tab", {
                    relatedTarget: t[0]
                })
                    , o = e.Event("show.bs.tab", {
                    relatedTarget: i[0]
                });
                i.trigger(s),
                    t.trigger(o);
                if (o.isDefaultPrevented() || s.isDefaultPrevented())
                    return;
                var u = e(r);
                this.activate(t.closest("li"), n),
                    this.activate(u, u.parent(), function() {
                        i.trigger({
                            type: "hidden.bs.tab",
                            relatedTarget: t[0]
                        }),
                            t.trigger({
                                type: "shown.bs.tab",
                                relatedTarget: i[0]
                            })
                    })
            }
            ,
            t.prototype.activate = function(n, r, i) {
                function u() {
                    s.removeClass("active").find("> .dropdown-menu > .active").removeClass("active").end().find('[data-toggle="tab"]').attr("aria-expanded", !1),
                        n.addClass("active").find('[data-toggle="tab"]').attr("aria-expanded", !0),
                        o ? (n[0].offsetWidth,
                            n.addClass("in")) : n.removeClass("fade"),
                    n.parent(".dropdown-menu").length && n.closest("li.dropdown").addClass("active").end().find('[data-toggle="tab"]').attr("aria-expanded", !0),
                    i && i()
                }
                var s = r.find("> .active")
                    , o = i && e.support.transition && (s.length && s.hasClass("fade") || !!r.find("> .fade").length);
                s.length && o ? s.one("bsTransitionEnd", u).emulateTransitionEnd(t.TRANSITION_DURATION) : u(),
                    s.removeClass("in")
            }
        ;
        var r = e.fn.tab;
        e.fn.tab = n,
            e.fn.tab.Constructor = t,
            e.fn.tab.noConflict = function() {
                return e.fn.tab = r,
                    this
            }
        ;
        var i = function(t) {
            t.preventDefault(),
                n.call(e(this), "show")
        };
        e(document).on("click.bs.tab.data-api", '[data-toggle="tab"]', i).on("click.bs.tab.data-api", '[data-toggle="pill"]', i)
    }(jQuery),
    +function(e) {
        function n(n) {
            return this.each(function() {
                var r = e(this)
                    , i = r.data("bs.affix")
                    , s = typeof n == "object" && n;
                i || r.data("bs.affix", i = new t(this,s)),
                typeof n == "string" && i[n]()
            })
        }
        var t = function(n, r) {
            this.options = e.extend({}, t.DEFAULTS, r),
                this.$target = e(this.options.target).on("scroll.bs.affix.data-api", e.proxy(this.checkPosition, this)).on("click.bs.affix.data-api", e.proxy(this.checkPositionWithEventLoop, this)),
                this.$element = e(n),
                this.affixed = null,
                this.unpin = null,
                this.pinnedOffset = null,
                this.checkPosition()
        };
        t.VERSION = "3.3.5",
            t.RESET = "affix affix-top affix-bottom",
            t.DEFAULTS = {
                offset: 0,
                target: window
            },
            t.prototype.getState = function(e, t, n, r) {
                var i = this.$target.scrollTop()
                    , s = this.$element.offset()
                    , o = this.$target.height();
                if (n != null && this.affixed == "top")
                    return i < n ? "top" : !1;
                if (this.affixed == "bottom")
                    return n != null ? i + this.unpin <= s.top ? !1 : "bottom" : i + o <= e - r ? !1 : "bottom";
                var u = this.affixed == null
                    , a = u ? i : s.top
                    , f = u ? o : t;
                return n != null && i <= n ? "top" : r != null && a + f >= e - r ? "bottom" : !1
            }
            ,
            t.prototype.getPinnedOffset = function() {
                if (this.pinnedOffset)
                    return this.pinnedOffset;
                this.$element.removeClass(t.RESET).addClass("affix");
                var e = this.$target.scrollTop()
                    , n = this.$element.offset();
                return this.pinnedOffset = n.top - e
            }
            ,
            t.prototype.checkPositionWithEventLoop = function() {
                setTimeout(e.proxy(this.checkPosition, this), 1)
            }
            ,
            t.prototype.checkPosition = function() {
                if (!this.$element.is(":visible"))
                    return;
                var n = this.$element.height()
                    , r = this.options.offset
                    , i = r.top
                    , s = r.bottom
                    , o = Math.max(e(document).height(), e(document.body).height());
                typeof r != "object" && (s = i = r),
                typeof i == "function" && (i = r.top(this.$element)),
                typeof s == "function" && (s = r.bottom(this.$element));
                var u = this.getState(o, n, i, s);
                if (this.affixed != u) {
                    this.unpin != null && this.$element.css("top", "");
                    var a = "affix" + (u ? "-" + u : "")
                        , f = e.Event(a + ".bs.affix");
                    this.$element.trigger(f);
                    if (f.isDefaultPrevented())
                        return;
                    this.affixed = u,
                        this.unpin = u == "bottom" ? this.getPinnedOffset() : null,
                        this.$element.removeClass(t.RESET).addClass(a).trigger(a.replace("affix", "affixed") + ".bs.affix")
                }
                u == "bottom" && this.$element.offset({
                    top: o - n - s
                })
            }
        ;
        var r = e.fn.affix;
        e.fn.affix = n,
            e.fn.affix.Constructor = t,
            e.fn.affix.noConflict = function() {
                return e.fn.affix = r,
                    this
            }
            ,
            e(window).on("load", function() {
                e('[data-spy="affix"]').each(function() {
                    var t = e(this)
                        , r = t.data();
                    r.offset = r.offset || {},
                    r.offsetBottom != null && (r.offset.bottom = r.offsetBottom),
                    r.offsetTop != null && (r.offset.top = r.offsetTop),
                        n.call(t, r)
                })
            })
    }(jQuery),
    define("bootstrap", function() {}),
    function(e, t) {
        if (e.initGeetest)
            return;
        var n = t.getElementsByTagName("head")[0], r = location.protocol + "//", i = [], s, o = function() {
            return parseInt(Math.random() * 1e4) + (new Date).valueOf()
        }, u = function() {
            for (var e = 0, t = i.length; e < t; e += 1)
                i[e]();
            i = []
        }, a = function() {
            return e.Geetest || t.getElementById("gt_lib")
        }, f = function() {
            var e = t.createElement("script");
            e.charset = "UTF-8",
                e.type = "text/javascript",
                e.onload = e.onreadystatechange = function() {
                    if (!this.readyState || this.readyState === "loaded" || this.readyState === "complete") {
                        if (!a())
                            throw s = "fail",
                                new Error("网络错误");
                        s = "loaded",
                            u(),
                            e.onload = e.onreadystatechange = null
                    }
                }
                ,
                e.onerror = function() {
                    throw s = "fail",
                        e.onerror = null,
                        new Error("网络错误")
                }
                ,
                e.src = r + "static.geetest.com/static/js/geetest.0.0.0.js",
                n.appendChild(e)
        };
        if (a())
            s = "loaded";
        else {
            s = "loading";
            var l = "geetest_" + o();
            e[l] = function() {
                s = "loaded",
                    u(),
                    e[l] = undefined;
                try {
                    delete e[l]
                } catch (t) {}
            }
            ;
            var c = t.createElement("script");
            c.charset = "UTF-8",
                c.type = "text/javascript",
                c.onload = c.onreadystatechange = function() {
                    if (!this.readyState || this.readyState === "loaded" || this.readyState === "complete")
                        a() || f()
                }
                ,
                c.onerror = f,
                c.src = r + "api.geetest.com/get.php?callback=" + l,
                n.appendChild(c)
        }
        e.initGeetest = function(t, n) {
            console.log(t);
            var r = function() {
                n(new e.Geetest(t))
            };
            if (s === "loaded")
                r();
            else {
                if (s === "fail")
                    throw new Error("网络错误");
                s === "loading" && i.push(function() {
                    r()
                })
            }
        }
    }(window, document),
    define("geetest", function() {}),
    define("toastr", ["jquery"], function(e) {
        return function() {
            function u(e, t, n) {
                return w({
                    type: i.error,
                    iconClass: E().iconClasses.error,
                    message: e,
                    optionsOverride: n,
                    title: t
                })
            }
            function a(n, r) {
                return n || (n = E()),
                    t = e("#" + n.containerId),
                    t.length ? t : (r && (t = g(n)),
                        t)
            }
            function f(e, t, n) {
                return w({
                    type: i.info,
                    iconClass: E().iconClasses.info,
                    message: e,
                    optionsOverride: n,
                    title: t
                })
            }
            function l(e) {
                n = e
            }
            function c(e, t, n) {
                return w({
                    type: i.success,
                    iconClass: E().iconClasses.success,
                    message: e,
                    optionsOverride: n,
                    title: t
                })
            }
            function h(e, t, n) {
                return w({
                    type: i.warning,
                    iconClass: E().iconClasses.warning,
                    message: e,
                    optionsOverride: n,
                    title: t
                })
            }
            function p(e, n) {
                var r = E();
                t || a(r),
                m(e, r, n) || v(r)
            }
            function d(n) {
                var r = E();
                t || a(r);
                if (n && e(":focus", n).length === 0) {
                    S(n);
                    return
                }
                t.children().length && t.remove()
            }
            function v(n) {
                var r = t.children();
                for (var i = r.length - 1; i >= 0; i--)
                    m(e(r[i]), n)
            }
            function m(t, n, r) {
                var i = r && r.force ? r.force : !1;
                return t && (i || e(":focus", t).length === 0) ? (t[n.hideMethod]({
                    duration: n.hideDuration,
                    easing: n.hideEasing,
                    complete: function() {
                        S(t)
                    }
                }),
                    !0) : !1
            }
            function g(n) {
                return t = e("<div/>").attr("id", n.containerId).addClass(n.positionClass).attr("aria-live", "polite").attr("role", "alert"),
                    t.appendTo(e(n.target)),
                    t
            }
            function y() {
                return {
                    tapToDismiss: !0,
                    toastClass: "toast",
                    containerId: "toast-container",
                    debug: !1,
                    showMethod: "fadeIn",
                    showDuration: 300,
                    showEasing: "swing",
                    onShown: undefined,
                    hideMethod: "fadeOut",
                    hideDuration: 1e3,
                    hideEasing: "swing",
                    onHidden: undefined,
                    closeMethod: !1,
                    closeDuration: !1,
                    closeEasing: !1,
                    extendedTimeOut: 1e3,
                    iconClasses: {
                        error: "toast-error",
                        info: "toast-info",
                        success: "toast-success",
                        warning: "toast-warning"
                    },
                    iconClass: "toast-info",
                    positionClass: "toast-top-right",
                    timeOut: 5e3,
                    titleClass: "toast-title",
                    messageClass: "toast-message",
                    escapeHtml: !1,
                    target: "body",
                    closeHtml: '<button type="button">&times;</button>',
                    newestOnTop: !0,
                    preventDuplicates: !1,
                    progressBar: !1
                }
            }
            function b(e) {
                if (!n)
                    return;
                n(e)
            }
            function w(n) {
                function m(e) {
                    return e == null && (e = ""),
                        (new String(e)).replace(/&/g, "&amp;").replace(/"/g, "&quot;").replace(/'/g, "&#39;").replace(/</g, "&lt;").replace(/>/g, "&gt;")
                }
                function g() {
                    x(),
                        N(),
                        C(),
                        k(),
                        L(),
                        T()
                }
                function y() {
                    f.hover(_, M),
                    !i.onclick && i.tapToDismiss && f.click(O),
                    i.closeButton && p && p.click(function(e) {
                        e.stopPropagation ? e.stopPropagation() : e.cancelBubble !== undefined && e.cancelBubble !== !0 && (e.cancelBubble = !0),
                            O(!0)
                    }),
                    i.onclick && f.click(function(e) {
                        i.onclick(e),
                            O()
                    })
                }
                function w() {
                    f.hide(),
                        f[i.showMethod]({
                            duration: i.showDuration,
                            easing: i.showEasing,
                            complete: i.onShown
                        }),
                    i.timeOut > 0 && (u = setTimeout(O, i.timeOut),
                        d.maxHideTime = parseFloat(i.timeOut),
                        d.hideEta = (new Date).getTime() + d.maxHideTime,
                    i.progressBar && (d.intervalId = setInterval(D, 10)))
                }
                function x() {
                    n.iconClass && f.addClass(i.toastClass).addClass(s)
                }
                function T() {
                    i.newestOnTop ? t.prepend(f) : t.append(f)
                }
                function N() {
                    n.title && (l.append(i.escapeHtml ? m(n.title) : n.title).addClass(i.titleClass),
                        f.append(l))
                }
                function C() {
                    n.message && (c.append(i.escapeHtml ? m(n.message) : n.message).addClass(i.messageClass),
                        f.append(c))
                }
                function k() {
                    i.closeButton && (p.addClass("toast-close-button").attr("role", "button"),
                        f.prepend(p))
                }
                function L() {
                    i.progressBar && (h.addClass("toast-progress"),
                        f.prepend(h))
                }
                function A(e, t) {
                    if (e.preventDuplicates) {
                        if (t.message === o)
                            return !0;
                        o = t.message
                    }
                    return !1
                }
                function O(t) {
                    var n = t && i.closeMethod !== !1 ? i.closeMethod : i.hideMethod
                        , r = t && i.closeDuration !== !1 ? i.closeDuration : i.hideDuration
                        , s = t && i.closeEasing !== !1 ? i.closeEasing : i.hideEasing;
                    if (e(":focus", f).length && !t)
                        return;
                    return clearTimeout(d.intervalId),
                        f[n]({
                            duration: r,
                            easing: s,
                            complete: function() {
                                S(f),
                                i.onHidden && v.state !== "hidden" && i.onHidden(),
                                    v.state = "hidden",
                                    v.endTime = new Date,
                                    b(v)
                            }
                        })
                }
                function M() {
                    if (i.timeOut > 0 || i.extendedTimeOut > 0)
                        u = setTimeout(O, i.extendedTimeOut),
                            d.maxHideTime = parseFloat(i.extendedTimeOut),
                            d.hideEta = (new Date).getTime() + d.maxHideTime
                }
                function _() {
                    clearTimeout(u),
                        d.hideEta = 0,
                        f.stop(!0, !0)[i.showMethod]({
                            duration: i.showDuration,
                            easing: i.showEasing
                        })
                }
                function D() {
                    var e = (d.hideEta - (new Date).getTime()) / d.maxHideTime * 100;
                    h.width(e + "%")
                }
                var i = E()
                    , s = n.iconClass || i.iconClass;
                typeof n.optionsOverride != "undefined" && (i = e.extend(i, n.optionsOverride),
                    s = n.optionsOverride.iconClass || s);
                if (A(i, n))
                    return;
                r++,
                    t = a(i, !0);
                var u = null
                    , f = e("<div/>")
                    , l = e("<div/>")
                    , c = e("<div/>")
                    , h = e("<div/>")
                    , p = e(i.closeHtml)
                    , d = {
                    intervalId: null,
                    hideEta: null,
                    maxHideTime: null
                }
                    , v = {
                    toastId: r,
                    state: "visible",
                    startTime: new Date,
                    options: i,
                    map: n
                };
                return g(),
                    w(),
                    y(),
                    b(v),
                i.debug && console && console.log(v),
                    f
            }
            function E() {
                return e.extend({}, y(), s.options)
            }
            function S(e) {
                t || (t = a());
                if (e.is(":visible"))
                    return;
                e.remove(),
                    e = null,
                t.children().length === 0 && (t.remove(),
                    o = undefined)
            }
            var t, n, r = 0, i = {
                error: "error",
                info: "info",
                success: "success",
                warning: "warning"
            }, s = {
                clear: p,
                remove: d,
                error: u,
                getContainer: a,
                info: f,
                options: {},
                subscribe: l,
                success: c,
                version: "2.1.2",
                warning: h
            }, o;
            return s
        }()
    }),
    function(e, t) {
        typeof define == "function" && define.amd ? define("bootbox", ["jquery"], t) : typeof exports == "object" ? module.exports = t(require("jquery")) : e.bootbox = t(e.jQuery)
    }(this, function e(t, n) {
        function o(e) {
            var t = m[i.locale];
            return t ? t[e] : m.en[e]
        }
        function u(e, n, r) {
            e.stopPropagation(),
                e.preventDefault();
            var i = t.isFunction(r) && r.call(n, e) === !1;
            i || n.modal("hide")
        }
        function a(e) {
            var t, n = 0;
            for (t in e)
                n++;
            return n
        }
        function f(e, n) {
            var r = 0;
            t.each(e, function(e, t) {
                n(e, t, r++)
            })
        }
        function l(e) {
            var n, r;
            if (typeof e != "object")
                throw new Error("Please supply an object of options");
            if (!e.message)
                throw new Error("Please specify a message");
            return e = t.extend({}, i, e),
            e.buttons || (e.buttons = {}),
                n = e.buttons,
                r = a(n),
                f(n, function(e, i, s) {
                    t.isFunction(i) && (i = n[e] = {
                        callback: i
                    });
                    if (t.type(i) !== "object")
                        throw new Error("button with key " + e + " must be an object");
                    i.label || (i.label = e),
                    i.className || (r <= 2 && s === r - 1 ? i.className = "btn-primary" : i.className = "btn-default")
                }),
                e
        }
        function c(e, t) {
            var n = e.length
                , r = {};
            if (n < 1 || n > 2)
                throw new Error("Invalid argument length");
            return n === 2 || typeof e[0] == "string" ? (r[t[0]] = e[0],
                r[t[1]] = e[1]) : r = e[0],
                r
        }
        function h(e, n, r) {
            return t.extend(!0, {}, e, c(n, r))
        }
        function p(e, t, n, r) {
            var i = {
                className: "bootbox-" + e,
                buttons: d.apply(null, t)
            };
            return v(h(i, r, n), t)
        }
        function d() {
            var e = {};
            for (var t = 0, n = arguments.length; t < n; t++) {
                var r = arguments[t]
                    , i = r.toLowerCase()
                    , s = r.toUpperCase();
                e[i] = {
                    label: o(s)
                }
            }
            return e
        }
        function v(e, t) {
            var r = {};
            return f(t, function(e, t) {
                r[t] = !0
            }),
                f(e.buttons, function(e) {
                    if (r[e] === n)
                        throw new Error("button key " + e + " is not allowed (options are " + t.join("\n") + ")")
                }),
                e
        }
        var r = {
            dialog: "<div class='bootbox modal' tabindex='-1' role='dialog'><div class='modal-dialog'><div class='modal-content'><div class='modal-body'><div class='bootbox-body'></div></div></div></div></div>",
            header: "<div class='modal-header'><h4 class='modal-title'></h4></div>",
            footer: "<div class='modal-footer'></div>",
            closeButton: "<button type='button' class='bootbox-close-button close' data-dismiss='modal' aria-hidden='true'>&times;</button>",
            form: "<form class='bootbox-form'></form>",
            inputs: {
                text: "<input class='bootbox-input bootbox-input-text form-control' autocomplete=off type=text />",
                textarea: "<textarea class='bootbox-input bootbox-input-textarea form-control'></textarea>",
                email: "<input class='bootbox-input bootbox-input-email form-control' autocomplete='off' type='email' />",
                select: "<select class='bootbox-input bootbox-input-select form-control'></select>",
                checkbox: "<div class='checkbox'><label><input class='bootbox-input bootbox-input-checkbox' type='checkbox' /></label></div>",
                date: "<input class='bootbox-input bootbox-input-date form-control' autocomplete=off type='date' />",
                time: "<input class='bootbox-input bootbox-input-time form-control' autocomplete=off type='time' />",
                number: "<input class='bootbox-input bootbox-input-number form-control' autocomplete=off type='number' />",
                password: "<input class='bootbox-input bootbox-input-password form-control' autocomplete='off' type='password' />"
            }
        }
            , i = {
            locale: "en",
            backdrop: "static",
            animate: !0,
            className: null,
            closeButton: !0,
            show: !0,
            container: "body"
        }
            , s = {};
        s.alert = function() {
            var e;
            e = p("alert", ["ok"], ["message", "callback"], arguments);
            if (e.callback && !t.isFunction(e.callback))
                throw new Error("alert requires callback property to be a function when provided");
            return e.buttons.ok.callback = e.onEscape = function() {
                return t.isFunction(e.callback) ? e.callback.call(this) : !0
            }
                ,
                s.dialog(e)
        }
            ,
            s.confirm = function() {
                var e;
                e = p("confirm", ["cancel", "confirm"], ["message", "callback"], arguments),
                    e.buttons.cancel.callback = e.onEscape = function() {
                        return e.callback.call(this, !1)
                    }
                    ,
                    e.buttons.confirm.callback = function() {
                        return e.callback.call(this, !0)
                    }
                ;
                if (!t.isFunction(e.callback))
                    throw new Error("confirm requires a callback");
                return s.dialog(e)
            }
            ,
            s.prompt = function() {
                var e, i, o, u, a, l, c;
                u = t(r.form),
                    i = {
                        className: "bootbox-prompt",
                        buttons: d("cancel", "confirm"),
                        value: "",
                        inputType: "text"
                    },
                    e = v(h(i, arguments, ["title", "callback"]), ["cancel", "confirm"]),
                    l = e.show === n ? !0 : e.show,
                    e.message = u,
                    e.buttons.cancel.callback = e.onEscape = function() {
                        return e.callback.call(this, null)
                    }
                    ,
                    e.buttons.confirm.callback = function() {
                        var n;
                        switch (e.inputType) {
                            case "text":
                            case "textarea":
                            case "email":
                            case "select":
                            case "date":
                            case "time":
                            case "number":
                            case "password":
                                n = a.val();
                                break;
                            case "checkbox":
                                var r = a.find("input:checked");
                                n = [],
                                    f(r, function(e, r) {
                                        n.push(t(r).val())
                                    })
                        }
                        return e.callback.call(this, n)
                    }
                    ,
                    e.show = !1;
                if (!e.title)
                    throw new Error("prompt requires a title");
                if (!t.isFunction(e.callback))
                    throw new Error("prompt requires a callback");
                if (!r.inputs[e.inputType])
                    throw new Error("invalid prompt type");
                a = t(r.inputs[e.inputType]);
                switch (e.inputType) {
                    case "text":
                    case "textarea":
                    case "email":
                    case "date":
                    case "time":
                    case "number":
                    case "password":
                        a.val(e.value);
                        break;
                    case "select":
                        var p = {};
                        c = e.inputOptions || [];
                        if (!t.isArray(c))
                            throw new Error("Please pass an array of input options");
                        if (!c.length)
                            throw new Error("prompt with select requires options");
                        f(c, function(e, r) {
                            var i = a;
                            if (r.value === n || r.text === n)
                                throw new Error("given options in wrong format");
                            r.group && (p[r.group] || (p[r.group] = t("<optgroup/>").attr("label", r.group)),
                                i = p[r.group]),
                                i.append("<option value='" + r.value + "'>" + r.text + "</option>")
                        }),
                            f(p, function(e, t) {
                                a.append(t)
                            }),
                            a.val(e.value);
                        break;
                    case "checkbox":
                        var m = t.isArray(e.value) ? e.value : [e.value];
                        c = e.inputOptions || [];
                        if (!c.length)
                            throw new Error("prompt with checkbox requires options");
                        if (!c[0].value || !c[0].text)
                            throw new Error("given options in wrong format");
                        a = t("<div/>"),
                            f(c, function(n, i) {
                                var s = t(r.inputs[e.inputType]);
                                s.find("input").attr("value", i.value),
                                    s.find("label").append(i.text),
                                    f(m, function(e, t) {
                                        t === i.value && s.find("input").prop("checked", !0)
                                    }),
                                    a.append(s)
                            })
                }
                return e.placeholder && a.attr("placeholder", e.placeholder),
                e.pattern && a.attr("pattern", e.pattern),
                e.maxlength && a.attr("maxlength", e.maxlength),
                    u.append(a),
                    u.on("submit", function(e) {
                        e.preventDefault(),
                            e.stopPropagation(),
                            o.find(".btn-primary").click()
                    }),
                    o = s.dialog(e),
                    o.off("shown.bs.modal"),
                    o.on("shown.bs.modal", function() {
                        a.focus()
                    }),
                l === !0 && o.modal("show"),
                    o
            }
            ,
            s.dialog = function(e) {
                e = l(e);
                var i = t(r.dialog)
                    , s = i.find(".modal-dialog")
                    , o = i.find(".modal-body")
                    , a = e.buttons
                    , c = ""
                    , h = {
                    onEscape: e.onEscape
                };
                if (t.fn.modal === n)
                    throw new Error("$.fn.modal is not defined; please double check you have included the Bootstrap JavaScript library. See http://getbootstrap.com/javascript/ for more details.");
                f(a, function(e, t) {
                    c += "<button data-bb-handler='" + e + "' type='button' class='btn " + t.className + "'>" + t.label + "</button>",
                        h[e] = t.callback
                }),
                    o.find(".bootbox-body").html(e.message),
                e.animate === !0 && i.addClass("fade"),
                e.className && i.addClass(e.className),
                    e.size === "large" ? s.addClass("modal-lg") : e.size === "small" && s.addClass("modal-sm"),
                e.title && o.before(r.header);
                if (e.closeButton) {
                    var p = t(r.closeButton);
                    e.title ? i.find(".modal-header").prepend(p) : p.css("margin-top", "-10px").prependTo(o)
                }
                return e.title && i.find(".modal-title").html(e.title),
                c.length && (o.after(r.footer),
                    i.find(".modal-footer").html(c)),
                    i.on("hidden.bs.modal", function(e) {
                        e.target === this && i.remove()
                    }),
                    i.on("shown.bs.modal", function() {
                        i.find(".btn-primary:first").focus()
                    }),
                e.backdrop !== "static" && i.on("click.dismiss.bs.modal", function(e) {
                    i.children(".modal-backdrop").length && (e.currentTarget = i.children(".modal-backdrop").get(0));
                    if (e.target !== e.currentTarget)
                        return;
                    i.trigger("escape.close.bb")
                }),
                    i.on("escape.close.bb", function(e) {
                        h.onEscape && u(e, i, h.onEscape)
                    }),
                    i.on("click", ".modal-footer button", function(e) {
                        var n = t(this).data("bb-handler");
                        u(e, i, h[n])
                    }),
                    i.on("click", ".bootbox-close-button", function(e) {
                        u(e, i, h.onEscape)
                    }),
                    i.on("keyup", function(e) {
                        e.which === 27 && i.trigger("escape.close.bb")
                    }),
                    t(e.container).append(i),
                    i.modal({
                        backdrop: e.backdrop == n ? "static" : e.backdrop,
                        keyboard: !1,
                        show: !1
                    }),
                e.show && i.modal("show"),
                    i
            }
            ,
            s.setDefaults = function() {
                var e = {};
                arguments.length === 2 ? e[arguments[0]] = arguments[1] : e = arguments[0],
                    t.extend(i, e)
            }
            ,
            s.hideAll = function() {
                return t(".bootbox").modal("hide"),
                    s
            }
        ;
        var m = {
            bg_BG: {
                OK: "Ок",
                CANCEL: "Отказ",
                CONFIRM: "Потвърждавам"
            },
            br: {
                OK: "OK",
                CANCEL: "Cancelar",
                CONFIRM: "Sim"
            },
            cs: {
                OK: "OK",
                CANCEL: "Zrušit",
                CONFIRM: "Potvrdit"
            },
            da: {
                OK: "OK",
                CANCEL: "Annuller",
                CONFIRM: "Accepter"
            },
            de: {
                OK: "OK",
                CANCEL: "Abbrechen",
                CONFIRM: "Akzeptieren"
            },
            el: {
                OK: "Εντάξει",
                CANCEL: "Ακύρωση",
                CONFIRM: "Επιβεβαίωση"
            },
            en: {
                OK: "OK",
                CANCEL: "Cancel",
                CONFIRM: "OK"
            },
            es: {
                OK: "OK",
                CANCEL: "Cancelar",
                CONFIRM: "Aceptar"
            },
            et: {
                OK: "OK",
                CANCEL: "Katkesta",
                CONFIRM: "OK"
            },
            fa: {
                OK: "قبول",
                CANCEL: "لغو",
                CONFIRM: "تایید"
            },
            fi: {
                OK: "OK",
                CANCEL: "Peruuta",
                CONFIRM: "OK"
            },
            fr: {
                OK: "OK",
                CANCEL: "Annuler",
                CONFIRM: "D'accord"
            },
            he: {
                OK: "אישור",
                CANCEL: "ביטול",
                CONFIRM: "אישור"
            },
            hu: {
                OK: "OK",
                CANCEL: "Mégsem",
                CONFIRM: "Megerősít"
            },
            hr: {
                OK: "OK",
                CANCEL: "Odustani",
                CONFIRM: "Potvrdi"
            },
            id: {
                OK: "OK",
                CANCEL: "Batal",
                CONFIRM: "OK"
            },
            it: {
                OK: "OK",
                CANCEL: "Annulla",
                CONFIRM: "Conferma"
            },
            ja: {
                OK: "OK",
                CANCEL: "キャンセル",
                CONFIRM: "確認"
            },
            lt: {
                OK: "Gerai",
                CANCEL: "Atšaukti",
                CONFIRM: "Patvirtinti"
            },
            lv: {
                OK: "Labi",
                CANCEL: "Atcelt",
                CONFIRM: "Apstiprināt"
            },
            nl: {
                OK: "OK",
                CANCEL: "Annuleren",
                CONFIRM: "Accepteren"
            },
            no: {
                OK: "OK",
                CANCEL: "Avbryt",
                CONFIRM: "OK"
            },
            pl: {
                OK: "OK",
                CANCEL: "Anuluj",
                CONFIRM: "Potwierdź"
            },
            pt: {
                OK: "OK",
                CANCEL: "Cancelar",
                CONFIRM: "Confirmar"
            },
            ru: {
                OK: "OK",
                CANCEL: "Отмена",
                CONFIRM: "Применить"
            },
            sq: {
                OK: "OK",
                CANCEL: "Anulo",
                CONFIRM: "Prano"
            },
            sv: {
                OK: "OK",
                CANCEL: "Avbryt",
                CONFIRM: "OK"
            },
            th: {
                OK: "ตกลง",
                CANCEL: "ยกเลิก",
                CONFIRM: "ยืนยัน"
            },
            tr: {
                OK: "Tamam",
                CANCEL: "İptal",
                CONFIRM: "Onayla"
            },
            zh_CN: {
                OK: "OK",
                CANCEL: "取消",
                CONFIRM: "确认"
            },
            zh_TW: {
                OK: "OK",
                CANCEL: "取消",
                CONFIRM: "確認"
            }
        };
        return s.addLocale = function(e, n) {
            return t.each(["OK", "CANCEL", "CONFIRM"], function(e, t) {
                if (!n[t])
                    throw new Error("Please supply a translation for '" + t + "'")
            }),
                m[e] = {
                    OK: n.OK,
                    CANCEL: n.CANCEL,
                    CONFIRM: n.CONFIRM
                },
                s
        }
            ,
            s.removeLocale = function(e) {
                return delete m[e],
                    s
            }
            ,
            s.setLocale = function(e) {
                return s.setDefaults("locale", e)
            }
            ,
            s.init = function(n) {
                return e(n || t)
            }
            ,
            s
    }),
    function() {
        function e(e) {
            function a(i, a) {
                var l, h, m = i == window, g = a && a.message !== undefined ? a.message : undefined;
                a = e.extend({}, e.blockUI.defaults, a || {});
                if (a.ignoreIfBlocked && e(i).data("blockUI.isBlocked"))
                    return;
                a.overlayCSS = e.extend({}, e.blockUI.defaults.overlayCSS, a.overlayCSS || {}),
                    l = e.extend({}, e.blockUI.defaults.css, a.css || {}),
                a.onOverlayClick && (a.overlayCSS.cursor = "pointer"),
                    h = e.extend({}, e.blockUI.defaults.themedCSS, a.themedCSS || {}),
                    g = g === undefined ? a.message : g,
                m && o && f(window, {
                    fadeOut: 0
                });
                if (g && typeof g != "string" && (g.parentNode || g.jquery)) {
                    var y = g.jquery ? g[0] : g
                        , b = {};
                    e(i).data("blockUI.history", b),
                        b.el = y,
                        b.parent = y.parentNode,
                        b.display = y.style.display,
                        b.position = y.style.position,
                    b.parent && b.parent.removeChild(y)
                }
                e(i).data("blockUI.onUnblock", a.onUnblock);
                var w = a.baseZ, E, S, x, T;
                n || a.forceIframe ? E = e('<iframe class="blockUI" style="z-index:' + w++ + ';display:none;border:none;margin:0;padding:0;position:absolute;width:100%;height:100%;top:0;left:0" src="' + a.iframeSrc + '"></iframe>') : E = e('<div class="blockUI" style="display:none"></div>'),
                    a.theme ? S = e('<div class="blockUI blockOverlay ui-widget-overlay" style="z-index:' + w++ + ';display:none"></div>') : S = e('<div class="blockUI blockOverlay" style="z-index:' + w++ + ';display:none;border:none;margin:0;padding:0;width:100%;height:100%;top:0;left:0"></div>'),
                    a.theme && m ? (T = '<div class="blockUI ' + a.blockMsgClass + ' blockPage ui-dialog ui-widget ui-corner-all" style="z-index:' + (w + 10) + ';display:none;position:fixed">',
                    a.title && (T += '<div class="ui-widget-header ui-dialog-titlebar ui-corner-all blockTitle">' + (a.title || "&nbsp;") + "</div>"),
                        T += '<div class="ui-widget-content ui-dialog-content"></div>',
                        T += "</div>") : a.theme ? (T = '<div class="blockUI ' + a.blockMsgClass + ' blockElement ui-dialog ui-widget ui-corner-all" style="z-index:' + (w + 10) + ';display:none;position:absolute">',
                    a.title && (T += '<div class="ui-widget-header ui-dialog-titlebar ui-corner-all blockTitle">' + (a.title || "&nbsp;") + "</div>"),
                        T += '<div class="ui-widget-content ui-dialog-content"></div>',
                        T += "</div>") : m ? T = '<div class="blockUI ' + a.blockMsgClass + ' blockPage" style="z-index:' + (w + 10) + ';display:none;position:fixed"></div>' : T = '<div class="blockUI ' + a.blockMsgClass + ' blockElement" style="z-index:' + (w + 10) + ';display:none;position:absolute"></div>',
                    x = e(T),
                g && (a.theme ? (x.css(h),
                    x.addClass("ui-widget-content")) : x.css(l)),
                a.theme || S.css(a.overlayCSS),
                    S.css("position", m ? "fixed" : "absolute"),
                (n || a.forceIframe) && E.css("opacity", 0);
                var N = [E, S, x]
                    , C = m ? e("body") : e(i);
                e.each(N, function() {
                    this.appendTo(C)
                }),
                a.theme && a.draggable && e.fn.draggable && x.draggable({
                    handle: ".ui-dialog-titlebar",
                    cancel: "li"
                });
                var k = s && (!e.support.boxModel || e("object,embed", m ? null : i).length > 0);
                if (r || k) {
                    m && a.allowBodyStretch && e.support.boxModel && e("html,body").css("height", "100%");
                    if ((r || !e.support.boxModel) && !m)
                        var L = v(i, "borderTopWidth")
                            , A = v(i, "borderLeftWidth")
                            , O = L ? "(0 - " + L + ")" : 0
                            , M = A ? "(0 - " + A + ")" : 0;
                    e.each(N, function(e, t) {
                        var n = t[0].style;
                        n.position = "absolute";
                        if (e < 2)
                            m ? n.setExpression("height", "Math.max(document.body.scrollHeight, document.body.offsetHeight) - (jQuery.support.boxModel?0:" + a.quirksmodeOffsetHack + ') + "px"') : n.setExpression("height", 'this.parentNode.offsetHeight + "px"'),
                                m ? n.setExpression("width", 'jQuery.support.boxModel && document.documentElement.clientWidth || document.body.clientWidth + "px"') : n.setExpression("width", 'this.parentNode.offsetWidth + "px"'),
                            M && n.setExpression("left", M),
                            O && n.setExpression("top", O);
                        else if (a.centerY)
                            m && n.setExpression("top", '(document.documentElement.clientHeight || document.body.clientHeight) / 2 - (this.offsetHeight / 2) + (blah = document.documentElement.scrollTop ? document.documentElement.scrollTop : document.body.scrollTop) + "px"'),
                                n.marginTop = 0;
                        else if (!a.centerY && m) {
                            var r = a.css && a.css.top ? parseInt(a.css.top, 10) : 0
                                , i = "((document.documentElement.scrollTop ? document.documentElement.scrollTop : document.body.scrollTop) + " + r + ') + "px"';
                            n.setExpression("top", i)
                        }
                    })
                }
                g && (a.theme ? x.find(".ui-widget-content").append(g) : x.append(g),
                (g.jquery || g.nodeType) && e(g).show()),
                (n || a.forceIframe) && a.showOverlay && E.show();
                if (a.fadeIn) {
                    var _ = a.onBlock ? a.onBlock : t
                        , D = a.showOverlay && !g ? _ : t
                        , P = g ? _ : t;
                    a.showOverlay && S._fadeIn(a.fadeIn, D),
                    g && x._fadeIn(a.fadeIn, P)
                } else
                    a.showOverlay && S.show(),
                    g && x.show(),
                    a.onBlock && a.onBlock.bind(x)();
                c(1, i, a),
                    m ? (o = x[0],
                        u = e(a.focusableElements, o),
                    a.focusInput && setTimeout(p, 20)) : d(x[0], a.centerX, a.centerY);
                if (a.timeout) {
                    var H = setTimeout(function() {
                        m ? e.unblockUI(a) : e(i).unblock(a)
                    }, a.timeout);
                    e(i).data("blockUI.timeout", H)
                }
            }
            function f(t, n) {
                var r, i = t == window, s = e(t), a = s.data("blockUI.history"), f = s.data("blockUI.timeout");
                f && (clearTimeout(f),
                    s.removeData("blockUI.timeout")),
                    n = e.extend({}, e.blockUI.defaults, n || {}),
                    c(0, t, n),
                n.onUnblock === null && (n.onUnblock = s.data("blockUI.onUnblock"),
                    s.removeData("blockUI.onUnblock"));
                var h;
                i ? h = e("body").children().filter(".blockUI").add("body > .blockUI") : h = s.find(">.blockUI"),
                n.cursorReset && (h.length > 1 && (h[1].style.cursor = n.cursorReset),
                h.length > 2 && (h[2].style.cursor = n.cursorReset)),
                i && (o = u = null),
                    n.fadeOut ? (r = h.length,
                        h.stop().fadeOut(n.fadeOut, function() {
                            --r === 0 && l(h, a, n, t)
                        })) : l(h, a, n, t)
            }
            function l(t, n, r, i) {
                var s = e(i);
                if (s.data("blockUI.isBlocked"))
                    return;
                t.each(function(e, t) {
                    this.parentNode && this.parentNode.removeChild(this)
                }),
                n && n.el && (n.el.style.display = n.display,
                    n.el.style.position = n.position,
                    n.el.style.cursor = "default",
                n.parent && n.parent.appendChild(n.el),
                    s.removeData("blockUI.history")),
                s.data("blockUI.static") && s.css("position", "static"),
                typeof r.onUnblock == "function" && r.onUnblock(i, r);
                var o = e(document.body)
                    , u = o.width()
                    , a = o[0].style.width;
                o.width(u - 1).width(u),
                    o[0].style.width = a
            }
            function c(t, n, r) {
                var i = n == window
                    , s = e(n);
                if (!t && (i && !o || !i && !s.data("blockUI.isBlocked")))
                    return;
                s.data("blockUI.isBlocked", t);
                if (!i || !r.bindEvents || t && !r.showOverlay)
                    return;
                var u = "mousedown mouseup keydown keypress keyup touchstart touchend touchmove";
                t ? e(document).bind(u, r, h) : e(document).unbind(u, h)
            }
            function h(t) {
                if (t.type === "keydown" && t.keyCode && t.keyCode == 9 && o && t.data.constrainTabKey) {
                    var n = u
                        , r = !t.shiftKey && t.target === n[n.length - 1]
                        , i = t.shiftKey && t.target === n[0];
                    if (r || i)
                        return setTimeout(function() {
                            p(i)
                        }, 10),
                            !1
                }
                var s = t.data
                    , a = e(t.target);
                return a.hasClass("blockOverlay") && s.onOverlayClick && s.onOverlayClick(t),
                    a.parents("div." + s.blockMsgClass).length > 0 ? !0 : a.parents().children().filter("div.blockUI").length === 0
            }
            function p(e) {
                if (!u)
                    return;
                var t = u[e === !0 ? u.length - 1 : 0];
                t && t.focus()
            }
            function d(e, t, n) {
                var r = e.parentNode
                    , i = e.style
                    , s = (r.offsetWidth - e.offsetWidth) / 2 - v(r, "borderLeftWidth")
                    , o = (r.offsetHeight - e.offsetHeight) / 2 - v(r, "borderTopWidth");
                t && (i.left = s > 0 ? s + "px" : "0"),
                n && (i.top = o > 0 ? o + "px" : "0")
            }
            function v(t, n) {
                return parseInt(e.css(t, n), 10) || 0
            }
            e.fn._fadeIn = e.fn.fadeIn;
            var t = e.noop || function() {}
                , n = /MSIE/.test(navigator.userAgent)
                , r = /MSIE 6.0/.test(navigator.userAgent) && !/MSIE 8.0/.test(navigator.userAgent)
                , i = document.documentMode || 0
                , s = e.isFunction(document.createElement("div").style.setExpression);
            e.blockUI = function(e) {
                a(window, e)
            }
                ,
                e.unblockUI = function(e) {
                    f(window, e)
                }
                ,
                e.growlUI = function(t, n, r, i) {
                    var s = e('<div class="growlUI"></div>');
                    t && s.append("<h1>" + t + "</h1>"),
                    n && s.append("<h2>" + n + "</h2>"),
                    r === undefined && (r = 3e3);
                    var o = function(t) {
                        t = t || {},
                            e.blockUI({
                                message: s,
                                fadeIn: typeof t.fadeIn != "undefined" ? t.fadeIn : 700,
                                fadeOut: typeof t.fadeOut != "undefined" ? t.fadeOut : 1e3,
                                timeout: typeof t.timeout != "undefined" ? t.timeout : r,
                                centerY: !1,
                                showOverlay: !1,
                                onUnblock: i,
                                css: e.blockUI.defaults.growlCSS
                            })
                    };
                    o();
                    var u = s.css("opacity");
                    s.mouseover(function() {
                        o({
                            fadeIn: 0,
                            timeout: 3e4
                        });
                        var t = e(".blockMsg");
                        t.stop(),
                            t.fadeTo(300, 1)
                    }).mouseout(function() {
                        e(".blockMsg").fadeOut(1e3)
                    })
                }
                ,
                e.fn.block = function(t) {
                    if (this[0] === window)
                        return e.blockUI(t),
                            this;
                    var n = e.extend({}, e.blockUI.defaults, t || {});
                    return this.each(function() {
                        var t = e(this);
                        if (n.ignoreIfBlocked && t.data("blockUI.isBlocked"))
                            return;
                        t.unblock({
                            fadeOut: 0
                        })
                    }),
                        this.each(function() {
                            e.css(this, "position") == "static" && (this.style.position = "relative",
                                e(this).data("blockUI.static", !0)),
                                this.style.zoom = 1,
                                a(this, t)
                        })
                }
                ,
                e.fn.unblock = function(t) {
                    return this[0] === window ? (e.unblockUI(t),
                        this) : this.each(function() {
                        f(this, t)
                    })
                }
                ,
                e.blockUI.version = 2.7,
                e.blockUI.defaults = {
                    message: "<h1>Please wait...</h1>",
                    title: null,
                    draggable: !0,
                    theme: !1,
                    css: {
                        padding: 0,
                        margin: 0,
                        width: "30%",
                        top: "40%",
                        left: "35%",
                        textAlign: "center",
                        color: "#000",
                        border: "3px solid #aaa",
                        backgroundColor: "#fff",
                        cursor: "wait"
                    },
                    themedCSS: {
                        width: "30%",
                        top: "40%",
                        left: "35%"
                    },
                    overlayCSS: {
                        backgroundColor: "#000",
                        opacity: .6,
                        cursor: "wait"
                    },
                    cursorReset: "default",
                    growlCSS: {
                        width: "350px",
                        top: "10px",
                        left: "",
                        right: "10px",
                        border: "none",
                        padding: "5px",
                        opacity: .6,
                        cursor: "default",
                        color: "#fff",
                        backgroundColor: "#000",
                        "-webkit-border-radius": "10px",
                        "-moz-border-radius": "10px",
                        "border-radius": "10px"
                    },
                    iframeSrc: /^https/i.test(window.location.href || "") ? "javascript:false" : "about:blank",
                    forceIframe: !1,
                    baseZ: 1e3,
                    centerX: !0,
                    centerY: !0,
                    allowBodyStretch: !0,
                    bindEvents: !0,
                    constrainTabKey: !0,
                    fadeIn: 200,
                    fadeOut: 400,
                    timeout: 0,
                    showOverlay: !0,
                    focusInput: !0,
                    focusableElements: ":input:enabled:visible",
                    onBlock: null,
                    onUnblock: null,
                    onOverlayClick: null,
                    quirksmodeOffsetHack: 4,
                    blockMsgClass: "blockMsg",
                    ignoreIfBlocked: !1
                };
            var o = null
                , u = []
        }
        typeof define == "function" && define.amd && define.amd.jQuery ? define("blockUI", ["jquery"], e) : e(jQuery)
    }(),
    define("uiService", ["jquery", "toastr", "bootbox", "blockUI"], function(e, t, n) {
        t.options.closeButton = !0,
            t.options.timeOut = 5e3,
            t.options.extendedTimeOut = 5e3,
            t.options.progressBar = !0,
            t.options.positionClass = "toast-top-center";
        var r = {
            init: function() {},
            hide: function() {},
            success: function(e, n) {
                n = n || "成功",
                    t.success(e, n)
            },
            error: function(e, n) {
                n = n || "错误",
                    t.error(e, n)
            },
            info: function(e, n) {
                n = n || "提醒",
                    t.info(e, n)
            },
            warning: function(e, n) {
                n = n || "警告",
                    t.warning(e, n)
            }
        }
            , i = {
            blockWithContent: function(t, n) {
                var r = {
                    message: t,
                    baseZ: 1100,
                    css: {
                        border: "0",
                        padding: "0",
                        backgroundColor: "none",
                        cursor: "default"
                    },
                    overlayCSS: {
                        backgroundColor: "#555",
                        opacity: .1,
                        cursor: "default"
                    }
                };
                n = e.extend({}, r, n || {}),
                    e.blockUI(n)
            },
            block: function() {
                var e = '<div class="block-spinner-bar"><div class="bounce1"></div><div class="bounce2"></div><div class="bounce3"></div></div>';
                this.blockWithContent(e)
            },
            unblock: function() {
                e.unblockUI()
            }
        };
        return n.setDefaults({
            locale: "zh_CN"
        }),
        {
            notification: r,
            blockUI: i,
            bootbox: n
        }
    }),
    function() {
        function e(e) {
            function t(t, n, r, i, s, o) {
                for (; s >= 0 && o > s; s += e) {
                    var u = i ? i[s] : s;
                    r = n(r, t[u], u, t)
                }
                return r
            }
            return function(n, r, i, s) {
                r = b(r, s, 4);
                var o = !C(n) && y.keys(n)
                    , u = (o || n).length
                    , a = e > 0 ? 0 : u - 1;
                return arguments.length < 3 && (i = n[o ? o[a] : a],
                    a += e),
                    t(n, r, i, o, a, u)
            }
        }
        function t(e) {
            return function(t, n, r) {
                n = w(n, r);
                for (var i = N(t), s = e > 0 ? 0 : i - 1; s >= 0 && i > s; s += e)
                    if (n(t[s], s, t))
                        return s;
                return -1
            }
        }
        function n(e, t, n) {
            return function(r, i, s) {
                var o = 0
                    , u = N(r);
                if ("number" == typeof s)
                    e > 0 ? o = s >= 0 ? s : Math.max(s + u, o) : u = s >= 0 ? Math.min(s + 1, u) : s + u + 1;
                else if (n && s && u)
                    return s = n(r, i),
                        r[s] === i ? s : -1;
                if (i !== i)
                    return s = t(l.call(r, o, u), y.isNaN),
                        s >= 0 ? s + o : -1;
                for (s = e > 0 ? o : u - 1; s >= 0 && u > s; s += e)
                    if (r[s] === i)
                        return s;
                return -1
            }
        }
        function r(e, t) {
            var n = M.length
                , r = e.constructor
                , i = y.isFunction(r) && r.prototype || u
                , s = "constructor";
            for (y.has(e, s) && !y.contains(t, s) && t.push(s); n--; )
                s = M[n],
                s in e && e[s] !== i[s] && !y.contains(t, s) && t.push(s)
        }
        var i = this
            , s = i._
            , o = Array.prototype
            , u = Object.prototype
            , a = Function.prototype
            , f = o.push
            , l = o.slice
            , c = u.toString
            , h = u.hasOwnProperty
            , p = Array.isArray
            , d = Object.keys
            , v = a.bind
            , m = Object.create
            , g = function() {}
            , y = function(e) {
            return e instanceof y ? e : this instanceof y ? void (this._wrapped = e) : new y(e)
        };
        "undefined" != typeof exports ? ("undefined" != typeof module && module.exports && (exports = module.exports = y),
            exports._ = y) : i._ = y,
            y.VERSION = "1.8.3";
        var b = function(e, t, n) {
            if (t === void 0)
                return e;
            switch (null == n ? 3 : n) {
                case 1:
                    return function(n) {
                        return e.call(t, n)
                    }
                        ;
                case 2:
                    return function(n, r) {
                        return e.call(t, n, r)
                    }
                        ;
                case 3:
                    return function(n, r, i) {
                        return e.call(t, n, r, i)
                    }
                        ;
                case 4:
                    return function(n, r, i, s) {
                        return e.call(t, n, r, i, s)
                    }
            }
            return function() {
                return e.apply(t, arguments)
            }
        }
            , w = function(e, t, n) {
            return null == e ? y.identity : y.isFunction(e) ? b(e, t, n) : y.isObject(e) ? y.matcher(e) : y.property(e)
        };
        y.iteratee = function(e, t) {
            return w(e, t, 1 / 0)
        }
        ;
        var E = function(e, t) {
            return function(n) {
                var r = arguments.length;
                if (2 > r || null == n)
                    return n;
                for (var i = 1; r > i; i++)
                    for (var s = arguments[i], o = e(s), u = o.length, a = 0; u > a; a++) {
                        var f = o[a];
                        t && n[f] !== void 0 || (n[f] = s[f])
                    }
                return n
            }
        }
            , S = function(e) {
            if (!y.isObject(e))
                return {};
            if (m)
                return m(e);
            g.prototype = e;
            var t = new g;
            return g.prototype = null,
                t
        }
            , x = function(e) {
            return function(t) {
                return null == t ? void 0 : t[e]
            }
        }
            , T = Math.pow(2, 53) - 1
            , N = x("length")
            , C = function(e) {
            var t = N(e);
            return "number" == typeof t && t >= 0 && T >= t
        };
        y.each = y.forEach = function(e, t, n) {
            t = b(t, n);
            var r, i;
            if (C(e))
                for (r = 0,
                         i = e.length; i > r; r++)
                    t(e[r], r, e);
            else {
                var s = y.keys(e);
                for (r = 0,
                         i = s.length; i > r; r++)
                    t(e[s[r]], s[r], e)
            }
            return e
        }
            ,
            y.map = y.collect = function(e, t, n) {
                t = w(t, n);
                for (var r = !C(e) && y.keys(e), i = (r || e).length, s = Array(i), o = 0; i > o; o++) {
                    var u = r ? r[o] : o;
                    s[o] = t(e[u], u, e)
                }
                return s
            }
            ,
            y.reduce = y.foldl = y.inject = e(1),
            y.reduceRight = y.foldr = e(-1),
            y.find = y.detect = function(e, t, n) {
                var r;
                return r = C(e) ? y.findIndex(e, t, n) : y.findKey(e, t, n),
                    r !== void 0 && r !== -1 ? e[r] : void 0
            }
            ,
            y.filter = y.select = function(e, t, n) {
                var r = [];
                return t = w(t, n),
                    y.each(e, function(e, n, i) {
                        t(e, n, i) && r.push(e)
                    }),
                    r
            }
            ,
            y.reject = function(e, t, n) {
                return y.filter(e, y.negate(w(t)), n)
            }
            ,
            y.every = y.all = function(e, t, n) {
                t = w(t, n);
                for (var r = !C(e) && y.keys(e), i = (r || e).length, s = 0; i > s; s++) {
                    var o = r ? r[s] : s;
                    if (!t(e[o], o, e))
                        return !1
                }
                return !0
            }
            ,
            y.some = y.any = function(e, t, n) {
                t = w(t, n);
                for (var r = !C(e) && y.keys(e), i = (r || e).length, s = 0; i > s; s++) {
                    var o = r ? r[s] : s;
                    if (t(e[o], o, e))
                        return !0
                }
                return !1
            }
            ,
            y.contains = y.includes = y.include = function(e, t, n, r) {
                return C(e) || (e = y.values(e)),
                ("number" != typeof n || r) && (n = 0),
                y.indexOf(e, t, n) >= 0
            }
            ,
            y.invoke = function(e, t) {
                var n = l.call(arguments, 2)
                    , r = y.isFunction(t);
                return y.map(e, function(e) {
                    var i = r ? t : e[t];
                    return null == i ? i : i.apply(e, n)
                })
            }
            ,
            y.pluck = function(e, t) {
                return y.map(e, y.property(t))
            }
            ,
            y.where = function(e, t) {
                return y.filter(e, y.matcher(t))
            }
            ,
            y.findWhere = function(e, t) {
                return y.find(e, y.matcher(t))
            }
            ,
            y.max = function(e, t, n) {
                var r, i, s = -1 / 0, o = -1 / 0;
                if (null == t && null != e) {
                    e = C(e) ? e : y.values(e);
                    for (var u = 0, a = e.length; a > u; u++)
                        r = e[u],
                        r > s && (s = r)
                } else
                    t = w(t, n),
                        y.each(e, function(e, n, r) {
                            i = t(e, n, r),
                            (i > o || i === -1 / 0 && s === -1 / 0) && (s = e,
                                o = i)
                        });
                return s
            }
            ,
            y.min = function(e, t, n) {
                var r, i, s = 1 / 0, o = 1 / 0;
                if (null == t && null != e) {
                    e = C(e) ? e : y.values(e);
                    for (var u = 0, a = e.length; a > u; u++)
                        r = e[u],
                        s > r && (s = r)
                } else
                    t = w(t, n),
                        y.each(e, function(e, n, r) {
                            i = t(e, n, r),
                            (o > i || 1 / 0 === i && 1 / 0 === s) && (s = e,
                                o = i)
                        });
                return s
            }
            ,
            y.shuffle = function(e) {
                for (var t, n = C(e) ? e : y.values(e), r = n.length, i = Array(r), s = 0; r > s; s++)
                    t = y.random(0, s),
                    t !== s && (i[s] = i[t]),
                        i[t] = n[s];
                return i
            }
            ,
            y.sample = function(e, t, n) {
                return null == t || n ? (C(e) || (e = y.values(e)),
                    e[y.random(e.length - 1)]) : y.shuffle(e).slice(0, Math.max(0, t))
            }
            ,
            y.sortBy = function(e, t, n) {
                return t = w(t, n),
                    y.pluck(y.map(e, function(e, n, r) {
                        return {
                            value: e,
                            index: n,
                            criteria: t(e, n, r)
                        }
                    }).sort(function(e, t) {
                        var n = e.criteria
                            , r = t.criteria;
                        if (n !== r) {
                            if (n > r || n === void 0)
                                return 1;
                            if (r > n || r === void 0)
                                return -1
                        }
                        return e.index - t.index
                    }), "value")
            }
        ;
        var k = function(e) {
            return function(t, n, r) {
                var i = {};
                return n = w(n, r),
                    y.each(t, function(r, s) {
                        var o = n(r, s, t);
                        e(i, r, o)
                    }),
                    i
            }
        };
        y.groupBy = k(function(e, t, n) {
            y.has(e, n) ? e[n].push(t) : e[n] = [t]
        }),
            y.indexBy = k(function(e, t, n) {
                e[n] = t
            }),
            y.countBy = k(function(e, t, n) {
                y.has(e, n) ? e[n]++ : e[n] = 1
            }),
            y.toArray = function(e) {
                return e ? y.isArray(e) ? l.call(e) : C(e) ? y.map(e, y.identity) : y.values(e) : []
            }
            ,
            y.size = function(e) {
                return null == e ? 0 : C(e) ? e.length : y.keys(e).length
            }
            ,
            y.partition = function(e, t, n) {
                t = w(t, n);
                var r = []
                    , i = [];
                return y.each(e, function(e, n, s) {
                    (t(e, n, s) ? r : i).push(e)
                }),
                    [r, i]
            }
            ,
            y.first = y.head = y.take = function(e, t, n) {
                return null == e ? void 0 : null == t || n ? e[0] : y.initial(e, e.length - t)
            }
            ,
            y.initial = function(e, t, n) {
                return l.call(e, 0, Math.max(0, e.length - (null == t || n ? 1 : t)))
            }
            ,
            y.last = function(e, t, n) {
                return null == e ? void 0 : null == t || n ? e[e.length - 1] : y.rest(e, Math.max(0, e.length - t))
            }
            ,
            y.rest = y.tail = y.drop = function(e, t, n) {
                return l.call(e, null == t || n ? 1 : t)
            }
            ,
            y.compact = function(e) {
                return y.filter(e, y.identity)
            }
        ;
        var L = function(e, t, n, r) {
            for (var i = [], s = 0, o = r || 0, u = N(e); u > o; o++) {
                var a = e[o];
                if (C(a) && (y.isArray(a) || y.isArguments(a))) {
                    t || (a = L(a, t, n));
                    var f = 0
                        , l = a.length;
                    for (i.length += l; l > f; )
                        i[s++] = a[f++]
                } else
                    n || (i[s++] = a)
            }
            return i
        };
        y.flatten = function(e, t) {
            return L(e, t, !1)
        }
            ,
            y.without = function(e) {
                return y.difference(e, l.call(arguments, 1))
            }
            ,
            y.uniq = y.unique = function(e, t, n, r) {
                y.isBoolean(t) || (r = n,
                    n = t,
                    t = !1),
                null != n && (n = w(n, r));
                for (var i = [], s = [], o = 0, u = N(e); u > o; o++) {
                    var a = e[o]
                        , f = n ? n(a, o, e) : a;
                    t ? (o && s === f || i.push(a),
                        s = f) : n ? y.contains(s, f) || (s.push(f),
                        i.push(a)) : y.contains(i, a) || i.push(a)
                }
                return i
            }
            ,
            y.union = function() {
                return y.uniq(L(arguments, !0, !0))
            }
            ,
            y.intersection = function(e) {
                for (var t = [], n = arguments.length, r = 0, i = N(e); i > r; r++) {
                    var s = e[r];
                    if (!y.contains(t, s)) {
                        for (var o = 1; n > o && y.contains(arguments[o], s); o++)
                            ;
                        o === n && t.push(s)
                    }
                }
                return t
            }
            ,
            y.difference = function(e) {
                var t = L(arguments, !0, !0, 1);
                return y.filter(e, function(e) {
                    return !y.contains(t, e)
                })
            }
            ,
            y.zip = function() {
                return y.unzip(arguments)
            }
            ,
            y.unzip = function(e) {
                for (var t = e && y.max(e, N).length || 0, n = Array(t), r = 0; t > r; r++)
                    n[r] = y.pluck(e, r);
                return n
            }
            ,
            y.object = function(e, t) {
                for (var n = {}, r = 0, i = N(e); i > r; r++)
                    t ? n[e[r]] = t[r] : n[e[r][0]] = e[r][1];
                return n
            }
            ,
            y.findIndex = t(1),
            y.findLastIndex = t(-1),
            y.sortedIndex = function(e, t, n, r) {
                n = w(n, r, 1);
                for (var i = n(t), s = 0, o = N(e); o > s; ) {
                    var u = Math.floor((s + o) / 2);
                    n(e[u]) < i ? s = u + 1 : o = u
                }
                return s
            }
            ,
            y.indexOf = n(1, y.findIndex, y.sortedIndex),
            y.lastIndexOf = n(-1, y.findLastIndex),
            y.range = function(e, t, n) {
                null == t && (t = e || 0,
                    e = 0),
                    n = n || 1;
                for (var r = Math.max(Math.ceil((t - e) / n), 0), i = Array(r), s = 0; r > s; s++,
                    e += n)
                    i[s] = e;
                return i
            }
        ;
        var A = function(e, t, n, r, i) {
            if (r instanceof t) {
                var s = S(e.prototype)
                    , o = e.apply(s, i);
                return y.isObject(o) ? o : s
            }
            return e.apply(n, i)
        };
        y.bind = function(e, t) {
            if (v && e.bind === v)
                return v.apply(e, l.call(arguments, 1));
            if (!y.isFunction(e))
                throw new TypeError("Bind must be called on a function");
            var n = l.call(arguments, 2)
                , r = function() {
                return A(e, r, t, this, n.concat(l.call(arguments)))
            };
            return r
        }
            ,
            y.partial = function(e) {
                var t = l.call(arguments, 1)
                    , n = function() {
                    for (var r = 0, i = t.length, s = Array(i), o = 0; i > o; o++)
                        s[o] = t[o] === y ? arguments[r++] : t[o];
                    for (; r < arguments.length; )
                        s.push(arguments[r++]);
                    return A(e, n, this, this, s)
                };
                return n
            }
            ,
            y.bindAll = function(e) {
                var t, n, r = arguments.length;
                if (1 >= r)
                    throw new Error("bindAll must be passed function names");
                for (t = 1; r > t; t++)
                    n = arguments[t],
                        e[n] = y.bind(e[n], e);
                return e
            }
            ,
            y.memoize = function(e, t) {
                var n = function(r) {
                    var i = n.cache
                        , s = "" + (t ? t.apply(this, arguments) : r);
                    return y.has(i, s) || (i[s] = e.apply(this, arguments)),
                        i[s]
                };
                return n.cache = {},
                    n
            }
            ,
            y.delay = function(e, t) {
                var n = l.call(arguments, 2);
                return setTimeout(function() {
                    return e.apply(null, n)
                }, t)
            }
            ,
            y.defer = y.partial(y.delay, y, 1),
            y.throttle = function(e, t, n) {
                var r, i, s, o = null, u = 0;
                n || (n = {});
                var a = function() {
                    u = n.leading === !1 ? 0 : y.now(),
                        o = null,
                        s = e.apply(r, i),
                    o || (r = i = null)
                };
                return function() {
                    var f = y.now();
                    u || n.leading !== !1 || (u = f);
                    var l = t - (f - u);
                    return r = this,
                        i = arguments,
                        0 >= l || l > t ? (o && (clearTimeout(o),
                            o = null),
                            u = f,
                            s = e.apply(r, i),
                        o || (r = i = null)) : o || n.trailing === !1 || (o = setTimeout(a, l)),
                        s
                }
            }
            ,
            y.debounce = function(e, t, n) {
                var r, i, s, o, u, a = function() {
                    var f = y.now() - o;
                    t > f && f >= 0 ? r = setTimeout(a, t - f) : (r = null,
                    n || (u = e.apply(s, i),
                    r || (s = i = null)))
                };
                return function() {
                    s = this,
                        i = arguments,
                        o = y.now();
                    var f = n && !r;
                    return r || (r = setTimeout(a, t)),
                    f && (u = e.apply(s, i),
                        s = i = null),
                        u
                }
            }
            ,
            y.wrap = function(e, t) {
                return y.partial(t, e)
            }
            ,
            y.negate = function(e) {
                return function() {
                    return !e.apply(this, arguments)
                }
            }
            ,
            y.compose = function() {
                var e = arguments
                    , t = e.length - 1;
                return function() {
                    for (var n = t, r = e[t].apply(this, arguments); n--; )
                        r = e[n].call(this, r);
                    return r
                }
            }
            ,
            y.after = function(e, t) {
                return function() {
                    return --e < 1 ? t.apply(this, arguments) : void 0
                }
            }
            ,
            y.before = function(e, t) {
                var n;
                return function() {
                    return --e > 0 && (n = t.apply(this, arguments)),
                    1 >= e && (t = null),
                        n
                }
            }
            ,
            y.once = y.partial(y.before, 2);
        var O = !{
            toString: null
        }.propertyIsEnumerable("toString")
            , M = ["valueOf", "isPrototypeOf", "toString", "propertyIsEnumerable", "hasOwnProperty", "toLocaleString"];
        y.keys = function(e) {
            if (!y.isObject(e))
                return [];
            if (d)
                return d(e);
            var t = [];
            for (var n in e)
                y.has(e, n) && t.push(n);
            return O && r(e, t),
                t
        }
            ,
            y.allKeys = function(e) {
                if (!y.isObject(e))
                    return [];
                var t = [];
                for (var n in e)
                    t.push(n);
                return O && r(e, t),
                    t
            }
            ,
            y.values = function(e) {
                for (var t = y.keys(e), n = t.length, r = Array(n), i = 0; n > i; i++)
                    r[i] = e[t[i]];
                return r
            }
            ,
            y.mapObject = function(e, t, n) {
                t = w(t, n);
                for (var r, i = y.keys(e), s = i.length, o = {}, u = 0; s > u; u++)
                    r = i[u],
                        o[r] = t(e[r], r, e);
                return o
            }
            ,
            y.pairs = function(e) {
                for (var t = y.keys(e), n = t.length, r = Array(n), i = 0; n > i; i++)
                    r[i] = [t[i], e[t[i]]];
                return r
            }
            ,
            y.invert = function(e) {
                for (var t = {}, n = y.keys(e), r = 0, i = n.length; i > r; r++)
                    t[e[n[r]]] = n[r];
                return t
            }
            ,
            y.functions = y.methods = function(e) {
                var t = [];
                for (var n in e)
                    y.isFunction(e[n]) && t.push(n);
                return t.sort()
            }
            ,
            y.extend = E(y.allKeys),
            y.extendOwn = y.assign = E(y.keys),
            y.findKey = function(e, t, n) {
                t = w(t, n);
                for (var r, i = y.keys(e), s = 0, o = i.length; o > s; s++)
                    if (r = i[s],
                            t(e[r], r, e))
                        return r
            }
            ,
            y.pick = function(e, t, n) {
                var r, i, s = {}, o = e;
                if (null == o)
                    return s;
                y.isFunction(t) ? (i = y.allKeys(o),
                    r = b(t, n)) : (i = L(arguments, !1, !1, 1),
                    r = function(e, t, n) {
                        return t in n
                    }
                    ,
                    o = Object(o));
                for (var u = 0, a = i.length; a > u; u++) {
                    var f = i[u]
                        , l = o[f];
                    r(l, f, o) && (s[f] = l)
                }
                return s
            }
            ,
            y.omit = function(e, t, n) {
                if (y.isFunction(t))
                    t = y.negate(t);
                else {
                    var r = y.map(L(arguments, !1, !1, 1), String);
                    t = function(e, t) {
                        return !y.contains(r, t)
                    }
                }
                return y.pick(e, t, n)
            }
            ,
            y.defaults = E(y.allKeys, !0),
            y.create = function(e, t) {
                var n = S(e);
                return t && y.extendOwn(n, t),
                    n
            }
            ,
            y.clone = function(e) {
                return y.isObject(e) ? y.isArray(e) ? e.slice() : y.extend({}, e) : e
            }
            ,
            y.tap = function(e, t) {
                return t(e),
                    e
            }
            ,
            y.isMatch = function(e, t) {
                var n = y.keys(t)
                    , r = n.length;
                if (null == e)
                    return !r;
                for (var i = Object(e), s = 0; r > s; s++) {
                    var o = n[s];
                    if (t[o] !== i[o] || !(o in i))
                        return !1
                }
                return !0
            }
        ;
        var _ = function(e, t, n, r) {
            if (e === t)
                return 0 !== e || 1 / e === 1 / t;
            if (null == e || null == t)
                return e === t;
            e instanceof y && (e = e._wrapped),
            t instanceof y && (t = t._wrapped);
            var i = c.call(e);
            if (i !== c.call(t))
                return !1;
            switch (i) {
                case "[object RegExp]":
                case "[object String]":
                    return "" + e == "" + t;
                case "[object Number]":
                    return +e !== +e ? +t !== +t : 0 === +e ? 1 / +e === 1 / t : +e === +t;
                case "[object Date]":
                case "[object Boolean]":
                    return +e === +t
            }
            var s = "[object Array]" === i;
            if (!s) {
                if ("object" != typeof e || "object" != typeof t)
                    return !1;
                var o = e.constructor
                    , u = t.constructor;
                if (o !== u && !(y.isFunction(o) && o instanceof o && y.isFunction(u) && u instanceof u) && "constructor"in e && "constructor"in t)
                    return !1
            }
            n = n || [],
                r = r || [];
            for (var a = n.length; a--; )
                if (n[a] === e)
                    return r[a] === t;
            if (n.push(e),
                    r.push(t),
                    s) {
                if (a = e.length,
                    a !== t.length)
                    return !1;
                for (; a--; )
                    if (!_(e[a], t[a], n, r))
                        return !1
            } else {
                var f, l = y.keys(e);
                if (a = l.length,
                    y.keys(t).length !== a)
                    return !1;
                for (; a--; )
                    if (f = l[a],
                        !y.has(t, f) || !_(e[f], t[f], n, r))
                        return !1
            }
            return n.pop(),
                r.pop(),
                !0
        };
        y.isEqual = function(e, t) {
            return _(e, t)
        }
            ,
            y.isEmpty = function(e) {
                return null == e ? !0 : C(e) && (y.isArray(e) || y.isString(e) || y.isArguments(e)) ? 0 === e.length : 0 === y.keys(e).length
            }
            ,
            y.isElement = function(e) {
                return !!e && 1 === e.nodeType
            }
            ,
            y.isArray = p || function(e) {
                    return "[object Array]" === c.call(e)
                }
            ,
            y.isObject = function(e) {
                var t = typeof e;
                return "function" === t || "object" === t && !!e
            }
            ,
            y.each(["Arguments", "Function", "String", "Number", "Date", "RegExp", "Error"], function(e) {
                y["is" + e] = function(t) {
                    return c.call(t) === "[object " + e + "]"
                }
            }),
        y.isArguments(arguments) || (y.isArguments = function(e) {
                return y.has(e, "callee")
            }
        ),
        "function" != typeof /./ && "object" != typeof Int8Array && (y.isFunction = function(e) {
                return "function" == typeof e || !1
            }
        ),
            y.isFinite = function(e) {
                return isFinite(e) && !isNaN(parseFloat(e))
            }
            ,
            y.isNaN = function(e) {
                return y.isNumber(e) && e !== +e
            }
            ,
            y.isBoolean = function(e) {
                return e === !0 || e === !1 || "[object Boolean]" === c.call(e)
            }
            ,
            y.isNull = function(e) {
                return null === e
            }
            ,
            y.isUndefined = function(e) {
                return e === void 0
            }
            ,
            y.has = function(e, t) {
                return null != e && h.call(e, t)
            }
            ,
            y.noConflict = function() {
                return i._ = s,
                    this
            }
            ,
            y.identity = function(e) {
                return e
            }
            ,
            y.constant = function(e) {
                return function() {
                    return e
                }
            }
            ,
            y.noop = function() {}
            ,
            y.property = x,
            y.propertyOf = function(e) {
                return null == e ? function() {}
                    : function(t) {
                    return e[t]
                }
            }
            ,
            y.matcher = y.matches = function(e) {
                return e = y.extendOwn({}, e),
                    function(t) {
                        return y.isMatch(t, e)
                    }
            }
            ,
            y.times = function(e, t, n) {
                var r = Array(Math.max(0, e));
                t = b(t, n, 1);
                for (var i = 0; e > i; i++)
                    r[i] = t(i);
                return r
            }
            ,
            y.random = function(e, t) {
                return null == t && (t = e,
                    e = 0),
                e + Math.floor(Math.random() * (t - e + 1))
            }
            ,
            y.now = Date.now || function() {
                    return (new Date).getTime()
                }
        ;
        var D = {
            "&": "&amp;",
            "<": "&lt;",
            ">": "&gt;",
            '"': "&quot;",
            "'": "&#x27;",
            "`": "&#x60;"
        }
            , P = y.invert(D)
            , H = function(e) {
            var t = function(t) {
                return e[t]
            }
                , n = "(?:" + y.keys(e).join("|") + ")"
                , r = RegExp(n)
                , i = RegExp(n, "g");
            return function(e) {
                return e = null == e ? "" : "" + e,
                    r.test(e) ? e.replace(i, t) : e
            }
        };
        y.escape = H(D),
            y.unescape = H(P),
            y.result = function(e, t, n) {
                var r = null == e ? void 0 : e[t];
                return r === void 0 && (r = n),
                    y.isFunction(r) ? r.call(e) : r
            }
        ;
        var B = 0;
        y.uniqueId = function(e) {
            var t = ++B + "";
            return e ? e + t : t
        }
            ,
            y.templateSettings = {
                evaluate: /<%([\s\S]+?)%>/g,
                interpolate: /<%=([\s\S]+?)%>/g,
                escape: /<%-([\s\S]+?)%>/g
            };
        var j = /(.)^/
            , F = {
            "'": "'",
            "\\": "\\",
            "\r": "r",
            "\n": "n",
            "\u2028": "u2028",
            "\u2029": "u2029"
        }
            , I = /\\|'|\r|\n|\u2028|\u2029/g
            , q = function(e) {
            return "\\" + F[e]
        };
        y.template = function(e, t, n) {
            !t && n && (t = n),
                t = y.defaults({}, t, y.templateSettings);
            var r = RegExp([(t.escape || j).source, (t.interpolate || j).source, (t.evaluate || j).source].join("|") + "|$", "g")
                , i = 0
                , s = "__p+='";
            e.replace(r, function(t, n, r, o, u) {
                return s += e.slice(i, u).replace(I, q),
                    i = u + t.length,
                    n ? s += "'+\n((__t=(" + n + "))==null?'':_.escape(__t))+\n'" : r ? s += "'+\n((__t=(" + r + "))==null?'':__t)+\n'" : o && (s += "';\n" + o + "\n__p+='"),
                    t
            }),
                s += "';\n",
            t.variable || (s = "with(obj||{}){\n" + s + "}\n"),
                s = "var __t,__p='',__j=Array.prototype.join,print=function(){__p+=__j.call(arguments,'');};\n" + s + "return __p;\n";
            try {
                var o = new Function(t.variable || "obj","_",s)
            } catch (u) {
                throw u.source = s,
                    u
            }
            var a = function(e) {
                return o.call(this, e, y)
            }
                , f = t.variable || "obj";
            return a.source = "function(" + f + "){\n" + s + "}",
                a
        }
            ,
            y.chain = function(e) {
                var t = y(e);
                return t._chain = !0,
                    t
            }
        ;
        var R = function(e, t) {
            return e._chain ? y(t).chain() : t
        };
        y.mixin = function(e) {
            y.each(y.functions(e), function(t) {
                var n = y[t] = e[t];
                y.prototype[t] = function() {
                    var e = [this._wrapped];
                    return f.apply(e, arguments),
                        R(this, n.apply(y, e))
                }
            })
        }
            ,
            y.mixin(y),
            y.each(["pop", "push", "reverse", "shift", "sort", "splice", "unshift"], function(e) {
                var t = o[e];
                y.prototype[e] = function() {
                    var n = this._wrapped;
                    return t.apply(n, arguments),
                    "shift" !== e && "splice" !== e || 0 !== n.length || delete n[0],
                        R(this, n)
                }
            }),
            y.each(["concat", "join", "slice"], function(e) {
                var t = o[e];
                y.prototype[e] = function() {
                    return R(this, t.apply(this._wrapped, arguments))
                }
            }),
            y.prototype.value = function() {
                return this._wrapped
            }
            ,
            y.prototype.valueOf = y.prototype.toJSON = y.prototype.value,
            y.prototype.toString = function() {
                return "" + this._wrapped
            }
            ,
        "function" == typeof define && define.amd && define("underscore", [], function() {
            return y
        })
    }
        .call(this),
    define("_", function(e) {
        return function() {
            var t, n;
            return t || e._
        }
    }(this)),
    define("validators", ["_"], function(e) {
        var t = function(e) {
            return e === "" || e === null || e === undefined
        }
            , n = {
            number: /^(?:-?\d+|-?\d{1,3}(?:,\d{3})+)?(?:\.\d+)?$/,
            email: /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/,
            mobile: /^1[3|4|5|7|8]\d{9}$/,
            telephone: /^(\(\d{3,4}\)|\d{3,4}-|\s)?\d{7,8}((-\d{1,5})|\(\d{1,5}\))?$/
        }
            , r = {
            required: {
                validator: function(e) {
                    return /^.+$/.test(e)
                },
                error: "必填"
            },
            number: {
                validator: function(e) {
                    return t(e) || n.number.test(e)
                },
                error: "只能输入数字"
            },
            email: {
                validator: function(e) {
                    return t(e) || n.email.test(e)
                },
                error: "电子邮件格式错误"
            },
            date: {
                validator: function(e) {
                    return t(e) || !/Invalid|NaN/.test((new Date(e)).toString())
                },
                error: "日期格式错误"
            },
            mobile: {
                validator: function(e) {
                    return t(e) || n.mobile.test(e)
                },
                error: "手机号码格式错误"
            },
            telephone: {
                validator: function(e) {
                    return t(e) || n.telephone.test(e)
                },
                error: "电话号码格式错误"
            },
            phone: {
                validator: function(e) {
                    return t(e) || n.mobile.test(e) || n.telephone.test(e)
                },
                error: "电话号码格式错误"
            },
            phoneOrMail: {
                validator: function(e) {
                    return t(e) || n.mobile.test(e) || n.telephone.test(e) || n.email.test(e)
                },
                error: "请输入电话号码或电子邮箱地址"
            },
            ChineseOrEnglish: {
                validator: function(e) {
                    return /^[a-z\u4e00-\u9fa5\t\x20]+$/i.test(e)
                },
                error: "抱歉，只能输入中英文"
            },
            forbiden: {
                validator: function(e) {
                    return !/(script)/.test(e)
                },
                error: "存在非法字符"
            }
        }
            , i = function() {
            return !0
        };
        return {
            rules: r,
            create: function(t) {
                if (t && e.isArray(t.validator)) {
                    var n = [];
                    return e.each(t.validator, function(e) {
                        var i = r[e];
                        if (i) {
                            var s = function(e) {
                                return i.validator(e) ? (t.error = "",
                                    !0) : (t.error = i.error,
                                    !1)
                            };
                            n.push(s)
                        }
                    }),
                        n.length > 0 ? function(e) {
                            for (var t = 0; t < n.length; ++t)
                                if (!n[t](e))
                                    return !1;
                            return !0
                        }
                            : i
                }
                return i
            },
            setup: function(e) {
                var t = this.create(e);
                return e.$watch("value", t),
                    t.modal = e,
                    t
            },
            init: function(t) {
                var n = this
                    , r = e.map(t, function(e) {
                    return n.setup(e)
                });
                return {
                    isValid: function() {
                        var e = !0;
                        for (var t = 0; t < r.length; ++t)
                            r[t](r[t].modal.value) || (e = !1);
                        return e
                    }
                }
            }
        }
    }),
    require(["avalon", "common", "uiService", "validators", "jquery", "bootstrap"], function(e, t, n, r, i) {
        i("#feedbackModal").modal({
            show: !1
        });
        var s = e.define({
            $id: "feedbackData",
            data: {
                category: {
                    value: "",
                    validator: ["required"],
                    error: ""
                },
                content: {
                    value: "",
                    validator: ["required", "forbiden"],
                    error: ""
                },
                contact: {
                    value: "",
                    validator: ["required", "phoneOrMail"],
                    error: ""
                }
            },
            submit: function() {
                o.isValid() && (n.blockUI.block(),
                    i.ajax({
                        type: "POST",
                        url: "/service/submitFeedback",
                        dataType: "json",
                        data: {
                            category: s.data.category.value,
                            content: s.data.content.value,
                            contact: s.data.contact.value
                        }
                    }).done(function(e) {
                        e.errcode ? n.notification.error(e.message, "错误！") : (i("#feedbackModal").modal("hide"),
                            n.notification.success("我们已经收到您的反馈信息，谢谢您的支持！", "非常感谢！"))
                    }).fail(function() {
                        n.notification.error("系统错误", "错误")
                    }).always(function() {
                        n.blockUI.unblock()
                    }))
            },
            select: function(e) {
                s.data.category.value = e
            },
            reset: function() {
                s.data.category.value = "",
                    s.data.content.value = ""
            }
        })
            , o = r.init([s.data.category, s.data.content, s.data.contact]);
        i("#feedback").click(function() {
            i("#feedbackModal").modal("show")
        }),
            i("#feedbackModal").on("shown.bs.modal", function(e) {}),
            i("#feedbackModal").on("hidden.bs.modal", function(e) {}),
            i(function() {
                (new t.Utility).request("feedback") && i("#feedbackModal").modal("show")
            })
    }),
    define("feedback", function() {}),
    define("placeholder", ["jquery"], function(e) {
        (function(e) {
            var t = "placeholder"
                , n = t in document.createElement("input");
            e.fn.placeholder = function(r) {
                return this.each(function() {
                    var i = e(this)
                        , s = i.attr("data-user-native") == "false" ? !1 : !0
                        , o = i.attr("data-hide-on-focus") == "false" ? !1 : !0;
                    typeof r == "string" && (r = {
                        text: r
                    });
                    var u = e.extend({
                        text: "",
                        style: {},
                        namespace: "placeholder",
                        useNative: s,
                        hideOnFocus: o
                    }, r || {});
                    u.text || (u.text = i.attr(t));
                    if (!u.useNative)
                        i.removeAttr(t);
                    else if (n) {
                        i.attr(t, u.text);
                        return
                    }
                    var a = i[0].clientWidth
                        , f = i[0].clientHeight
                        , l = i.height()
                        , c = ["marginTop", "marginLeft", "paddingTop", "paddingLeft", "paddingRight"]
                        , h = function() {
                        E.show()
                    }
                        , p = function() {
                        E.hide()
                    }
                        , d = function() {
                        return !i.val()
                    }
                        , v = function() {
                        d() ? h() : p()
                    }
                        , m = i[0].tagName
                        , g = function() {
                        var t = i.position();
                        u.hideOnFocus || (t.left += 2),
                            E.css(t),
                            e.each(c, function(e, t) {
                                E.css(t, i.css(t))
                            })
                    }
                        , y = {
                        color: "gray",
                        cursor: "text",
                        textAlign: "left",
                        position: "absolute",
                        fontSize: i.css("fontSize"),
                        fontFamily: i.css("fontFamily"),
                        display: d() ? "block" : "none",
                        zIndex: 10,
                        "line-height": m == "TEXTAREA" ? "auto" : l + "px"
                    }
                        , b = {
                        text: u.text,
                        width: a,
                        height: f,
                        id: "inputPlaceholderKey"
                    }
                        , w = "." + u.namespace
                        , E = i.data("layer" + w);
                    E ? E.html(u.text) : i.data("layer" + w, E = e("<div>", b).appendTo(i.offsetParent())),
                        E.css(e.extend(y, u.style)).unbind("click" + w).bind("click" + w, function() {
                            u.hideOnFocus && p(),
                                i.focus()
                        }),
                        i.unbind(w).bind("blur" + w, v),
                        u.hideOnFocus ? i.bind("focus" + w, p) : i.bind("keypress keydown" + w, function(e) {
                            var t = e.keyCode;
                            (e.charCode || t >= 65 && t <= 90) && p()
                        }).bind("keyup" + w, v),
                        i.get(0).onpropertychange = v,
                        g(),
                        v()
                })
            }
        })(e)
    }),
    define("login", ["avalon", "common", "geetest", "feedback", "uiService", "statistics", "bootstrap", "placeholder"], function(e, t, n, r, i, s) {
        function d(e) {
            p.isSendEnable = !1;
            var n = t.Config.ajaxBaseUrl + "sendValidateToken"
                , r = "0";
            p.contentType == 2 && (r = "1");
            var i = {
                mobile: p.txtHandphoneNo,
                IP: "",
                isRegister: r,
                token: t.gtn(),
                gttoken: e
            };
            $.post(n, i, function(e) {
                e.status == 2 || e.data.status == 2 ? (p.hpError = e.data.message,
                    p.hpHighlight = !0,
                    p.isSendEnable = !0) : (p.verifyError = e.data.message,
                    p.isSendEnable = !0),
                e.data.status == 0 && (p.isSendEnable = !0,
                    p.time())
            })
        }
        function v(e) {
            $("#captcha").html("<div style='color:red; text-align: center;height:50px'>" + e.data.message + " 或 点击<a id='loginFeedbackBtn' href='javascript:;' >意见反馈</a>联系客服</div>")
        }
        function g(e) {
            $("#info").html("<div style='color:red; text-align: center;height:50px'>" + e.data.message + " 或 点击<a id='loginFeedbackBtn' href='javascript:;' >意见反馈</a>联系客服</div>")
        }
        function y() {
            var e = p.contentType == 1 ? "float" : "popup"
                , t = p.contentType == 1 ? p.loginHandler : p.tokenHandler
                , n = p.contentType == 1 ? v : g;
            $("#captcha").html(""),
                $("#captcha").show(),
                $.ajax({
                    url: "/service/gtregister?t=" + (new Date).getTime(),
                    type: "get",
                    dataType: "json",
                    success: function(r) {
                        r.status == 0 ? initGeetest({
                            gt: r.data.gt,
                            challenge: r.data.challenge,
                            product: e,
                            offline: !r.data.success
                        }, t) : n(r)
                    }
                })
        }
        function b() {
            p.hpError = "",
                p.hpHighlight = !1,
                p.passError = "",
                p.passHighlight = !1,
                p.verifyError = "",
                p.verifyHighlight = !1,
                p.txtHandphoneNo = "",
                p.txtPassword = "",
                p.txtVerifyCode = "",
                p.validError = ""
        }
        function w(e) {
            var t = !0;
            return p.txtHandphoneNo == "" ? (t = !1,
                p.hpError = "请输入手机号",
                p.hpHighlight = !0) : p.txtHandphoneNo.match(/^(13[0-9]|14[0-9]|15[0-9]|17[0-9]|18[0-9])\d{8}$/) ? (p.hpError = "",
                p.hpHighlight = !1) : (t = !1,
                p.hpError = "手机号不正确",
                p.hpHighlight = !0),
                p.txtPassword == "" ? (t = !1,
                    p.passError = "请输入密码",
                    p.passHighlight = !0) : e == 1 ? (p.txtPassword ? (p.passError = "",
                    p.passHighlight = !1) : (t = !1,
                    p.passError = "请输入密码",
                    p.passHighlight = !0),
                p.gtInvalid && (t = !1,
                    p.validError = "请拖动滑块完成验证")) : p.txtPassword.length < 8 ? (t = !1,
                    p.passError = "密码长度不正确，应为8~20位",
                    p.passHighlight = !0) : p.txtPassword.match(h) ? (p.passError = "",
                    p.passHighlight = !1) : (t = !1,
                    p.passError = "密码格式不正确，密码由8~20位数字和英文字母组成",
                    p.passHighlight = !0),
                t
        }
        function E(e) {
            e == 1 ? ($("#loginForm").show(),
                $("#registerContent").hide(),
                $("#resetContent").hide(),
                $("#successContent").hide()) : e == 2 ? ($("#registerContent").show(),
                $("#loginForm").hide(),
                $("#resetContent").hide(),
                $("#successContent").hide()) : e == 3 ? ($("#registerContent").hide(),
                $("#loginForm").hide(),
                $("#resetContent").show(),
                $("#successContent").hide()) : ($("#registerContent").hide(),
                $("#loginForm").hide(),
                $("#resetContent").hide(),
                $("#successContent").show(),
                $("#successContent").removeClass("hidden")),
                p.eventType = e,
                b()
        }
        function S() {
            $("#txtPass1, #txtPass2").popover({
                container: "body",
                trigger: "focus",
                html: !0,
                placement: "right",
                content: $("#popoverContent").html()
            }),
                $(".login-wrapper [data-toggle='popover']").popover({
                    placement: function(e, t) {
                        return $(window).height() < 750 ? "top" : "bottom"
                    }
                }),
                $("#loginModal,.login-content").keydown(function() {})
        }
        var o = new t.Utility
            , u = $("input[name=account]").val() || ""
            , a = $("input[name=password]").val() || ""
            , f = 0
            , l = 0
            , c = !1
            , h = /^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,20}$/
            , p = e.define({
            $id: "loginandregister",
            validError: "",
            hpError: "",
            passError: "",
            verifyError: "",
            hpHighlight: !1,
            passHighlight: !1,
            verifyHighlight: !1,
            txtHandphoneNo: u,
            txtPassword: a,
            txtVerifyCode: "",
            isSendEnable: !0,
            btnVerifyCodeTxt: "获取验证码",
            captchaSrc: "/images/loading.gif",
            md5String: "",
            successMessage: "",
            eventType: -1,
            redirectURL: "",
            contentType: 0,
            isLoading: !1,
            loginText: "登录",
            registerText: "会员注册",
            confirmText: "确定",
            isPopup: !1,
            formSubmit: function(e) {
                return e.preventDefault(),
                    p.isLoading ? !1 : !1
            },
            onRegisterClick: function(e) {
                if (p.isLoading)
                    return !1;
                if (w(2)) {
                    p.registerText = "注册中...",
                        p.isLoading = !0;
                    var n = t.Config.ajaxBaseUrl + "registerUser"
                        , r = {
                        userAcct: p.txtHandphoneNo,
                        userPassword: p.txtPassword,
                        userQrCode: p.txtVerifyCode.toLowerCase()
                    };
                    $.post(n, r, function(e) {
                        if (e.data.status == "0") {
                            p.registerText = "注册成功，自动跳转中...";
                            var t = e.data.data.uid
                                , n = e.data.data.title
                                , r = e.data.data.domain;
                            n || (n = ""),
                            r || (r = "");
                            var i = o.request("returnUrl");
                            if (!i) {
                                i = "/";
                                var u = o.browser.versions.mobile || o.browser.versions.android || o.browser.versions.ios;
                                u && (i += "?from=wap")
                            }
                            var a = "/identity?uid=" + encodeURIComponent(t) + "&title=" + encodeURIComponent(n) + "&domain=" + encodeURIComponent(r) + "&method=1";
                            a += "&returnUrl=" + encodeURIComponent(i),
                                s.track("注册成功", {}, function() {
                                    window.location = a
                                })
                        } else
                            e.data.status == "1" ? (p.hpError = e.data.message,
                                p.hpHighlight = !0,
                                p.registerText = "会员注册",
                                p.isLoading = !1) : e.data.status == "2" ? (p.passError = e.data.message,
                                p.passHighlight = !0,
                                p.registerText = "会员注册",
                                p.isLoading = !1) : (p.verifyError = e.data.message,
                                p.verifyHighlight = !0,
                                p.registerText = "会员注册",
                                p.isLoading = !1)
                    })
                }
            },
            onLoginClick: function(e) {
                if (p.isLoading)
                    return !1;
                if (w(1)) {
                    var n = t.Config.ajaxBaseUrl + "login"
                        , r = {
                        userAcct: p.txtHandphoneNo,
                        userPassword: p.txtPassword,
                        token: token
                    };
                    p.loginTimer(),
                        p.isLoading = !0,
                        $.post(n, r, function(e) {
                            if (e.data.status == "0") {
                                if (e.data.entAuthUrl) {
                                    window.location = e.data.entAuthUrl;
                                    return
                                }
                                var t = e.data.data.uid
                                    , n = e.data.data.title
                                    , r = e.data.data.domain;
                                n || (n = ""),
                                r || (r = "");
                                if (p.isPopup) {
                                    x.beforeLogin();
                                    var i = "/identity?uid=" + encodeURIComponent(t) + "&title=" + encodeURIComponent(n) + "&domain=" + encodeURIComponent(r) + "&method=1";
                                    i += "&returnUrl=" + encodeURIComponent(window.location.href),
                                        window.location = i
                                } else {
                                    var s = o.request("returnUrl");
                                    if (!s) {
                                        s = "/";
                                        var u = o.browser.versions.mobile || o.browser.versions.android || o.browser.versions.ios;
                                        u && (s += "?from=wap")
                                    }
                                    var i = "/identity?uid=" + encodeURIComponent(t) + "&title=" + encodeURIComponent(n) + "&domain=" + encodeURIComponent(r) + "&method=1";
                                    i += "&returnUrl=" + encodeURIComponent(s),
                                        window.location = i
                                }
                            } else
                                e.data.status == "1" ? (p.hpError = e.data.message,
                                    p.hpHighlight = !0,
                                    p.loginText = "登录",
                                    p.isLoading = !1,
                                    clearTimeout(l)) : e.data.status == "2" ? (p.passError = e.data.message,
                                    p.passHighlight = !0,
                                    p.loginText = "登录",
                                    p.isLoading = !1,
                                    clearTimeout(l)) : (p.verifyError = e.data.message,
                                    p.verifyHighlight = !0,
                                    p.loginText = "登录",
                                    p.isLoading = !1,
                                    clearTimeout(l))
                        })
                }
            },
            onResetPassword: function(e) {
                console.log("aaaaaaaaa");
                if (p.isLoading)
                    return !1;
                if (w(3)) {
                    var n = t.Config.ajaxBaseUrl + "resetPassword"
                        , r = {
                        userAcct: p.txtHandphoneNo,
                        userQrCode: p.txtVerifyCode,
                        userPassword: p.txtPassword
                    };
                    p.isLoading = !0,
                        p.confirmText = "重置密码中...",
                        $.post(n, r, function(e) {
                            if (e.data.status == "0") {
                                if (e.data.entAuthUrl) {
                                    window.location = e.data.entAuthUrl;
                                    return
                                }
                                p.confirmText = "重置密码成功，自动登录中...";
                                var t = e.data.data.uid
                                    , n = e.data.data.title
                                    , r = e.data.data.domain;
                                n || (n = ""),
                                r || (r = "");
                                var i = o.request("returnUrl");
                                if (!i) {
                                    i = "/";
                                    var s = o.browser.versions.mobile || o.browser.versions.android || o.browser.versions.ios;
                                    s && (i += "?from=wap")
                                }
                                var u = "/identity?uid=" + encodeURIComponent(t) + "&title=" + encodeURIComponent(n) + "&domain=" + encodeURIComponent(r) + "&method=1";
                                u += "&returnUrl=" + encodeURIComponent(i),
                                    window.location = u
                            } else
                                e.data.status == "1" ? (p.hpError = e.data.message,
                                    p.hpHighlight = !0,
                                    p.isLoading = !1,
                                    p.confirmText = "确定") : e.data.status == "2" ? (p.passError = e.data.message,
                                    p.passHighlight = !0,
                                    p.isLoading = !1,
                                    p.confirmText = "确定") : e.data.status == "3" && (p.verifyError = e.data.message,
                                    p.verifyHighlight = !0,
                                    p.isLoading = !1,
                                    p.confirmText = "确定")
                        })
                }
            },
            onSendVerifyCode: function(e) {
                p.hpError = "",
                    p.hpHighlight = !1;
                if (p.txtHandphoneNo == "")
                    return p.hpError = "请输入手机号",
                        p.hpHighlight = !0,
                        !1;
                if (!p.txtHandphoneNo.match(/^(13[0-9]|14[0-9]|15[0-9]|17[0-9]|18[0-9])\d{8}$/))
                    return p.hpError = "手机号不正确",
                        p.hpHighlight = !0,
                        !1;
                s.track("注册-获取验证码", {
                    "手机号": p.txtHandphoneNo
                }),
                    $("#validBtn").trigger("click")
            },
            onHPFocus: function(e) {
                p.hpError = "",
                    p.hpHighlight = !1
            },
            onPasswordFocus: function(e) {
                p.passError = "",
                    p.passHighlight = !1
            },
            onVerifyCodeFocus: function(e) {
                p.verifyError = "",
                    p.verifyHighlight = !1
            },
            wait: 60,
            time: function() {
                if (p.wait == 0)
                    p.isSendEnable = !0,
                        p.btnVerifyCodeTxt = "获取验证码",
                        p.wait = 60,
                        clearTimeout(f);
                else {
                    p.isSendEnable = !1;
                    var e = "重新发送(" + p.wait + ")";
                    p.btnVerifyCodeTxt = e,
                        p.wait--,
                        f = setTimeout(function() {
                            p.time()
                        }, 1e3)
                }
            },
            onLoginDisplay: function(e) {
                window.location.href = "/login"
            },
            onCloseClick: function() {
                $("#loginModal").modal("hide")
            },
            loginMsg: "",
            loadGeetest: function(e) {
                e && y(),
                c || y()
            },
            init: function(e) {
                e && (p.txtHandphoneNo = e.account,
                    p.txtPassword = e.password)
            },
            loginWait: 0,
            loginTimer: function() {
                p.loginWait == 2 ? (p.loginText = "努力加载中..请稍候",
                    clearTimeout(l)) : (p.loginText = "登录中...",
                    p.loginWait++,
                    l = setTimeout(function() {
                        p.loginTimer()
                    }, 1e3))
            },
            qqlogin: function(e) {
                e.preventDefault();
                var t = $("#thirdlogin").val()
                    , n = JSON.parse(t)
                    , r = ""
                    , i = window.location.href.split("?returnURL=");
                i.length > 1 ? r = i[1] : r = n.qqlogin.qxb_default_redirect;
                var s = n.qqlogin.redirect_page + "?returnUrl=" + encodeURIComponent(r)
                    , o = n.qqlogin.appid
                    , u = "https://graph.qq.com/oauth2.0/authorize?"
                    , a = ["client_id=" + o, "redirect_uri=" + encodeURIComponent(s), "scope=get_user_info", "response_type=code"]
                    , f = a.join("&")
                    , l = u + f;
                window.location.href = l
            },
            qqlogin2: function(e) {
                e.preventDefault();
                var t = $("#thirdlogin").val()
                    , n = JSON.parse(t)
                    , r = n.qqlogin.redirect_page + "?returnUrl=" + encodeURIComponent(encodeURIComponent(window.location.href))
                    , i = n.qqlogin.appid
                    , s = "https://graph.qq.com/oauth2.0/authorize?"
                    , o = ["client_id=" + i, "redirect_uri=" + encodeURIComponent(r), "scope=get_user_info", "response_type=code"]
                    , u = o.join("&")
                    , a = s + u;
                window.location.href = a
            },
            wxlogin: function(e) {
                e.preventDefault();
                var t = $("#thirdlogin").val()
                    , n = JSON.parse(t)
                    , r = ""
                    , i = window.location.href.split("?returnURL=");
                i.length > 1 ? r = i[1] : r = n.wxlogin.qxb_default_redirect;
                var s = n.wxlogin.redirect_page + "?returnUrl=" + r
                    , o = n.wxlogin.appid
                    , u = "https://open.weixin.qq.com/connect/qrconnect?"
                    , a = ["appid=" + o, "redirect_uri=" + encodeURIComponent(s), "scope=snsapi_login", "response_type=code"]
                    , f = a.join("&")
                    , l = u + f;
                window.location.href = l
            },
            wxlogin2: function(e) {
                e.preventDefault();
                var t = $("#thirdlogin").val()
                    , n = JSON.parse(t)
                    , r = n.wxlogin.redirect_page + "?returnUrl=" + encodeURIComponent(window.location.href)
                    , i = n.wxlogin.appid
                    , s = "https://open.weixin.qq.com/connect/qrconnect?"
                    , o = ["appid=" + i, "redirect_uri=" + encodeURIComponent(r), "scope=snsapi_login", "response_type=code"]
                    , u = o.join("&")
                    , a = s + u;
                window.location.href = a
            },
            goToRegister: function(e) {
                e.preventDefault();
                var t = "/register?returnURL=" + encodeURIComponent(window.location.href);
                window.location.href = t
            },
            goToForgetPassword: function(e) {
                e.preventDefault();
                var t = "/forget?returnURL=" + encodeURIComponent(window.location.href);
                window.location.href = t
            },
            validate: "",
            gtInvalid: !0,
            loginHandler: function(e) {
                c = !0,
                    e.appendTo("#captcha"),
                    e.onSuccess(function() {
                        p.validError = "正在验证中...",
                            p.validate = e.getValidate(),
                            token = p.validate.geetest_seccode,
                            $.ajax({
                                url: "/service/gtloginvalidate",
                                type: "post",
                                dataType: "json",
                                data: p.validate,
                                success: function(e) {
                                    e.status === "success" ? (p.gtInvalid = !1,
                                        p.validError = "") : (m.gtInvalid = !0,
                                        p.validError = "验证失败，请重新验证",
                                        p.loadGeetest(!0))
                                }
                            })
                    }),
                    e.onFail(function() {
                        p.gtInvalid = !0,
                            p.validError = "验证失败，请重新验证",
                            p.loadGeetest(!0)
                    }),
                    e.onError(function() {
                        p.gtInvalid = !0,
                            p.validError = "请确保网络处于正常连接状态 或者 刷新页面后再试"
                    })
            },
            tokenHandler: function(e) {
                c = !0,
                    e.appendTo(document.body),
                    e.bindOn(document.getElementById("validBtn")),
                    e.onSuccess(function() {
                        p.validError = "正在验证中...",
                            p.validate = e.getValidate(),
                            token = p.validate.geetest_seccode,
                            $.ajax({
                                url: "/service/gttokenvalidate",
                                type: "post",
                                dataType: "json",
                                data: p.validate,
                                success: function(e) {
                                    e.status === "success" ? (p.gtInvalid = !1,
                                        p.validError = "",
                                        d(token)) : (p.gtInvalid = !0,
                                        p.validError = "验证失败，请重新验证",
                                        p.loadGeetest(!0))
                                }
                            })
                    }),
                    e.onFail(function() {
                        p.gtInvalid = !0,
                            p.validError = "验证失败，请重新验证",
                            p.loadGeetest(!0)
                    }),
                    e.onError(function() {
                        p.gtInvalid = !0,
                            p.validError = "请确保网络处于正常连接状态 或者 刷新页面后再试"
                    })
            },
            isDownloadPopoverShow: !1,
            initPopover: function() {
                $("#btn-download-app").popover({
                    trigger: "hover"
                }).on("show.bs.popover", function() {
                    p.isDownloadPopoverShow = !0
                }).on("hide.bs.popover", function() {
                    p.isDownloadPopoverShow = !1
                })
            }
        });
        $(document).delegate("#loginFeedbackBtn", "click", function() {
            $("#feedbackModal").modal("show")
        }),
            p.initPopover(),
        typeof login_mode != "undefined" && (login_mode == 0 && (p.contentType = 1,
            p.loadGeetest()),
        login_mode == 1 && (p.contentType = 2,
            p.loadGeetest()),
        login_mode == 2 && (p.contentType = 3,
            p.loadGeetest())),
            S.prototype.setRedirectUrl = function(e) {
                p.redirectURL = e
            }
            ,
            S.prototype.open = function() {
                window.location.pathname == "/" ? window.location.href = "/login" : window.location.href = "/login?returnURL=" + encodeURIComponent(window.location.href)
            }
            ,
            S.prototype.popup = function() {
                E(1),
                    $("#loginModal").modal("show")
            }
            ,
            S.prototype.register = function() {
                window.location.href = "/register"
            }
            ,
            S.prototype.openWithMsg = function(e) {
                window.location.href = "/login"
            }
            ,
            S.prototype.init = function(e) {
                p.init(e)
            }
            ,
            S.prototype.qqlogin = function(e) {
                p.qqlogin(e)
            }
            ,
            S.prototype.wxlogin = function(e) {
                p.wxlogin(e)
            }
            ,
            S.prototype.onBeforeLogin = function() {}
            ,
            S.prototype.onHide = function() {}
            ,
            S.prototype.beforeLogin = function() {
                this.onBeforeLogin()
            }
            ,
            S.prototype.hide = function() {
                this.onHide()
            }
        ;
        var x = new S;
        return $("#loginModal").on("shown.bs.modal", function(e) {
            $("#loginForm input[placeholder]").placeholder(),
                p.contentType = 1,
                p.loadGeetest(),
                p.isPopup = !0
        }),
            $("#loginModal").on("hidden.bs.modal", function(e) {
                x.hide()
            }),
            $(document).on("login-required", function() {
                x.popup()
            }),
            x
    }),
    function(e, t) {
        typeof define == "function" && define.amd ? define("bloodhound", ["jquery"], function(n) {
            return e.Bloodhound = t(n)
        }) : typeof exports == "object" ? module.exports = t(require("jquery")) : e.Bloodhound = t(jQuery)
    }(this, function(e) {
        var t = function() {
            return {
                isMsie: function() {
                    return /(msie|trident)/i.test(navigator.userAgent) ? navigator.userAgent.match(/(msie |rv:)(\d+(.\d+)?)/i)[2] : !1
                },
                isBlankString: function(e) {
                    return !e || /^\s*$/.test(e)
                },
                escapeRegExChars: function(e) {
                    return e.replace(/[\-\[\]\/\{\}\(\)\*\+\?\.\\\^\$\|]/g, "\\$&")
                },
                isString: function(e) {
                    return typeof e == "string"
                },
                isNumber: function(e) {
                    return typeof e == "number"
                },
                isArray: e.isArray,
                isFunction: e.isFunction,
                isObject: e.isPlainObject,
                isUndefined: function(e) {
                    return typeof e == "undefined"
                },
                isElement: function(e) {
                    return !!e && e.nodeType === 1
                },
                isJQuery: function(t) {
                    return t instanceof e
                },
                toStr: function(n) {
                    return t.isUndefined(n) || n === null ? "" : n + ""
                },
                bind: e.proxy,
                each: function(t, n) {
                    function r(e, t) {
                        return n(t, e)
                    }
                    e.each(t, r)
                },
                map: e.map,
                filter: e.grep,
                every: function(t, n) {
                    var r = !0;
                    return t ? (e.each(t, function(e, i) {
                        if (!(r = n.call(null, i, e, t)))
                            return !1
                    }),
                        !!r) : r
                },
                some: function(t, n) {
                    var r = !1;
                    return t ? (e.each(t, function(e, i) {
                        if (r = n.call(null, i, e, t))
                            return !1
                    }),
                        !!r) : r
                },
                mixin: e.extend,
                identity: function(e) {
                    return e
                },
                clone: function(t) {
                    return e.extend(!0, {}, t)
                },
                getIdGenerator: function() {
                    var e = 0;
                    return function() {
                        return e++
                    }
                },
                templatify: function(n) {
                    function r() {
                        return String(n)
                    }
                    return e.isFunction(n) ? n : r
                },
                defer: function(e) {
                    setTimeout(e, 0)
                },
                debounce: function(e, t, n) {
                    var r, i;
                    return function() {
                        var s = this, o = arguments, u, a;
                        return u = function() {
                            r = null,
                            n || (i = e.apply(s, o))
                        }
                            ,
                            a = n && !r,
                            clearTimeout(r),
                            r = setTimeout(u, t),
                        a && (i = e.apply(s, o)),
                            i
                    }
                },
                throttle: function(e, t) {
                    var n, r, i, s, o, u;
                    return o = 0,
                        u = function() {
                            o = new Date,
                                i = null,
                                s = e.apply(n, r)
                        }
                        ,
                        function() {
                            var a = new Date
                                , f = t - (a - o);
                            return n = this,
                                r = arguments,
                                f <= 0 ? (clearTimeout(i),
                                    i = null,
                                    o = a,
                                    s = e.apply(n, r)) : i || (i = setTimeout(u, f)),
                                s
                        }
                },
                stringify: function(e) {
                    return t.isString(e) ? e : JSON.stringify(e)
                },
                noop: function() {}
            }
        }()
            , n = "0.11.1"
            , r = function() {
            function e(e) {
                return e = t.toStr(e),
                    e ? e.split(/\s+/) : []
            }
            function n(e) {
                return e = t.toStr(e),
                    e ? e.split(/\W+/) : []
            }
            function r(e) {
                return function(r) {
                    return r = t.isArray(r) ? r : [].slice.call(arguments, 0),
                        function(i) {
                            var s = [];
                            return t.each(r, function(n) {
                                s = s.concat(e(t.toStr(i[n])))
                            }),
                                s
                        }
                }
            }
            return {
                nonword: n,
                whitespace: e,
                obj: {
                    nonword: r(n),
                    whitespace: r(e)
                }
            }
        }()
            , i = function() {
            function n(n) {
                this.maxSize = t.isNumber(n) ? n : 100,
                    this.reset(),
                this.maxSize <= 0 && (this.set = this.get = e.noop)
            }
            function r() {
                this.head = this.tail = null
            }
            function i(e, t) {
                this.key = e,
                    this.val = t,
                    this.prev = this.next = null
            }
            return t.mixin(n.prototype, {
                set: function(t, n) {
                    var r = this.list.tail, s;
                    this.size >= this.maxSize && (this.list.remove(r),
                        delete this.hash[r.key],
                        this.size--),
                        (s = this.hash[t]) ? (s.val = n,
                            this.list.moveToFront(s)) : (s = new i(t,n),
                            this.list.add(s),
                            this.hash[t] = s,
                            this.size++)
                },
                get: function(t) {
                    var n = this.hash[t];
                    if (n)
                        return this.list.moveToFront(n),
                            n.val
                },
                reset: function() {
                    this.size = 0,
                        this.hash = {},
                        this.list = new r
                }
            }),
                t.mixin(r.prototype, {
                    add: function(t) {
                        this.head && (t.next = this.head,
                            this.head.prev = t),
                            this.head = t,
                            this.tail = this.tail || t
                    },
                    remove: function(t) {
                        t.prev ? t.prev.next = t.next : this.head = t.next,
                            t.next ? t.next.prev = t.prev : this.tail = t.prev
                    },
                    moveToFront: function(e) {
                        this.remove(e),
                            this.add(e)
                    }
                }),
                n
        }()
            , s = function() {
            function i(e, r) {
                this.prefix = ["__", e, "__"].join(""),
                    this.ttlKey = "__ttl__",
                    this.keyMatcher = new RegExp("^" + t.escapeRegExChars(this.prefix)),
                    this.ls = r || n,
                !this.ls && this._noop()
            }
            function s() {
                return (new Date).getTime()
            }
            function o(e) {
                return JSON.stringify(t.isUndefined(e) ? null : e)
            }
            function u(t) {
                return e.parseJSON(t)
            }
            function a(e) {
                var t, r, i = [], s = n.length;
                for (t = 0; t < s; t++)
                    (r = n.key(t)).match(e) && i.push(r.replace(e, ""));
                return i
            }
            var n;
            try {
                n = window.localStorage,
                    n.setItem("~~~", "!"),
                    n.removeItem("~~~")
            } catch (r) {
                n = null
            }
            return t.mixin(i.prototype, {
                _prefix: function(e) {
                    return this.prefix + e
                },
                _ttlKey: function(e) {
                    return this._prefix(e) + this.ttlKey
                },
                _noop: function() {
                    this.get = this.set = this.remove = this.clear = this.isExpired = t.noop
                },
                _safeSet: function(e, t) {
                    try {
                        this.ls.setItem(e, t)
                    } catch (n) {
                        n.name === "QuotaExceededError" && (this.clear(),
                            this._noop())
                    }
                },
                get: function(e) {
                    return this.isExpired(e) && this.remove(e),
                        u(this.ls.getItem(this._prefix(e)))
                },
                set: function(e, n, r) {
                    return t.isNumber(r) ? this._safeSet(this._ttlKey(e), o(s() + r)) : this.ls.removeItem(this._ttlKey(e)),
                        this._safeSet(this._prefix(e), o(n))
                },
                remove: function(e) {
                    return this.ls.removeItem(this._ttlKey(e)),
                        this.ls.removeItem(this._prefix(e)),
                        this
                },
                clear: function() {
                    var e, t = a(this.keyMatcher);
                    for (e = t.length; e--; )
                        this.remove(t[e]);
                    return this
                },
                isExpired: function(e) {
                    var n = u(this.ls.getItem(this._ttlKey(e)));
                    return t.isNumber(n) && s() > n ? !0 : !1
                }
            }),
                i
        }()
            , o = function() {
            function u(e) {
                e = e || {},
                    this.cancelled = !1,
                    this.lastReq = null,
                    this._send = e.transport,
                    this._get = e.limiter ? e.limiter(this._get) : this._get,
                    this._cache = e.cache === !1 ? new i(0) : o
            }
            var n = 0
                , r = {}
                , s = 6
                , o = new i(10);
            return u.setMaxPendingRequests = function(t) {
                s = t
            }
                ,
                u.resetCache = function() {
                    o.reset()
                }
                ,
                t.mixin(u.prototype, {
                    _fingerprint: function(n) {
                        return n = n || {},
                        n.url + n.type + e.param(n.data || {})
                    },
                    _get: function(e, t) {
                        function a(e) {
                            t(null, e),
                                i._cache.set(o, e)
                        }
                        function f() {
                            t(!0)
                        }
                        function l() {
                            n--,
                                delete r[o],
                            i.onDeckRequestArgs && (i._get.apply(i, i.onDeckRequestArgs),
                                i.onDeckRequestArgs = null)
                        }
                        var i = this, o, u;
                        o = this._fingerprint(e);
                        if (this.cancelled || o !== this.lastReq)
                            return;
                        (u = r[o]) ? u.done(a).fail(f) : n < s ? (n++,
                            r[o] = this._send(e).done(a).fail(f).always(l)) : this.onDeckRequestArgs = [].slice.call(arguments, 0)
                    },
                    get: function(n, r) {
                        var i, s;
                        r = r || e.noop,
                            n = t.isString(n) ? {
                                url: n
                            } : n || {},
                            s = this._fingerprint(n),
                            this.cancelled = !1,
                            this.lastReq = s,
                            (i = this._cache.get(s)) ? r(null, i) : this._get(n, r)
                    },
                    cancel: function() {
                        this.cancelled = !0
                    }
                }),
                u
        }()
            , u = window.SearchIndex = function() {
            function i(n) {
                n = n || {},
                (!n.datumTokenizer || !n.queryTokenizer) && e.error("datumTokenizer and queryTokenizer are both required"),
                    this.identify = n.identify || t.stringify,
                    this.datumTokenizer = n.datumTokenizer,
                    this.queryTokenizer = n.queryTokenizer,
                    this.reset()
            }
            function s(e) {
                return e = t.filter(e, function(e) {
                    return !!e
                }),
                    e = t.map(e, function(e) {
                        return e.toLowerCase()
                    }),
                    e
            }
            function o() {
                var e = {};
                return e[r] = [],
                    e[n] = {},
                    e
            }
            function u(e) {
                var t = {}
                    , n = [];
                for (var r = 0, i = e.length; r < i; r++)
                    t[e[r]] || (t[e[r]] = !0,
                        n.push(e[r]));
                return n
            }
            function a(e, t) {
                var n = 0
                    , r = 0
                    , i = [];
                e = e.sort(),
                    t = t.sort();
                var s = e.length
                    , o = t.length;
                while (n < s && r < o)
                    e[n] < t[r] ? n++ : e[n] > t[r] ? r++ : (i.push(e[n]),
                        n++,
                        r++);
                return i
            }
            var n = "c"
                , r = "i";
            return t.mixin(i.prototype, {
                bootstrap: function(t) {
                    this.datums = t.datums,
                        this.trie = t.trie
                },
                add: function(e) {
                    var i = this;
                    e = t.isArray(e) ? e : [e],
                        t.each(e, function(e) {
                            var u, a;
                            i.datums[u = i.identify(e)] = e,
                                a = s(i.datumTokenizer(e)),
                                t.each(a, function(e) {
                                    var t, s, a;
                                    t = i.trie,
                                        s = e.split("");
                                    while (a = s.shift())
                                        t = t[n][a] || (t[n][a] = o()),
                                            t[r].push(u)
                                })
                        })
                },
                get: function(n) {
                    var r = this;
                    return t.map(n, function(e) {
                        return r.datums[e]
                    })
                },
                search: function(i) {
                    var o = this, f, l;
                    return f = s(this.queryTokenizer(i)),
                        t.each(f, function(e) {
                            var t, i, s, u;
                            if (l && l.length === 0)
                                return !1;
                            t = o.trie,
                                i = e.split("");
                            while (t && (s = i.shift()))
                                t = t[n][s];
                            if (!t || i.length !== 0)
                                return l = [],
                                    !1;
                            u = t[r].slice(0),
                                l = l ? a(l, u) : u
                        }),
                        l ? t.map(u(l), function(e) {
                            return o.datums[e]
                        }) : []
                },
                all: function() {
                    var t = [];
                    for (var n in this.datums)
                        t.push(this.datums[n]);
                    return t
                },
                reset: function() {
                    this.datums = {},
                        this.trie = o()
                },
                serialize: function() {
                    return {
                        datums: this.datums,
                        trie: this.trie
                    }
                }
            }),
                i
        }()
            , a = function() {
            function n(e) {
                this.url = e.url,
                    this.ttl = e.ttl,
                    this.cache = e.cache,
                    this.prepare = e.prepare,
                    this.transform = e.transform,
                    this.transport = e.transport,
                    this.thumbprint = e.thumbprint,
                    this.storage = new s(e.cacheKey)
            }
            var e;
            return e = {
                data: "data",
                protocol: "protocol",
                thumbprint: "thumbprint"
            },
                t.mixin(n.prototype, {
                    _settings: function() {
                        return {
                            url: this.url,
                            type: "GET",
                            dataType: "json"
                        }
                    },
                    store: function(n) {
                        if (!this.cache)
                            return;
                        this.storage.set(e.data, n, this.ttl),
                            this.storage.set(e.protocol, location.protocol, this.ttl),
                            this.storage.set(e.thumbprint, this.thumbprint, this.ttl)
                    },
                    fromCache: function() {
                        var n = {}, r;
                        return this.cache ? (n.data = this.storage.get(e.data),
                            n.protocol = this.storage.get(e.protocol),
                            n.thumbprint = this.storage.get(e.thumbprint),
                            r = n.thumbprint !== this.thumbprint || n.protocol !== location.protocol,
                            n.data && !r ? n.data : null) : null
                    },
                    fromNetwork: function(e) {
                        function r() {
                            e(!0)
                        }
                        function i(n) {
                            e(null, t.transform(n))
                        }
                        var t = this, n;
                        if (!e)
                            return;
                        n = this.prepare(this._settings()),
                            this.transport(n).fail(r).done(i)
                    },
                    clear: function() {
                        return this.storage.clear(),
                            this
                    }
                }),
                n
        }()
            , f = function() {
            function e(e) {
                this.url = e.url,
                    this.prepare = e.prepare,
                    this.transform = e.transform,
                    this.transport = new o({
                        cache: e.cache,
                        limiter: e.limiter,
                        transport: e.transport
                    })
            }
            return t.mixin(e.prototype, {
                _settings: function() {
                    return {
                        url: this.url,
                        type: "GET",
                        dataType: "json"
                    }
                },
                get: function(t, n) {
                    function s(e, t) {
                        e ? n([]) : n(r.transform(t))
                    }
                    var r = this, i;
                    if (!n)
                        return;
                    return t = t || "",
                        i = this.prepare(t, this._settings()),
                        this.transport.get(i, s)
                },
                cancelLastRequest: function() {
                    this.transport.cancel()
                }
            }),
                e
        }()
            , l = function() {
            function r(r) {
                var i;
                return r ? (i = {
                    url: null,
                    ttl: 864e5,
                    cache: !0,
                    cacheKey: null,
                    thumbprint: "",
                    prepare: t.identity,
                    transform: t.identity,
                    transport: null
                },
                    r = t.isString(r) ? {
                        url: r
                    } : r,
                    r = t.mixin(i, r),
                !r.url && e.error("prefetch requires url to be set"),
                    r.transform = r.filter || r.transform,
                    r.cacheKey = r.cacheKey || r.url,
                    r.thumbprint = n + r.thumbprint,
                    r.transport = r.transport ? u(r.transport) : e.ajax,
                    r) : null
            }
            function i(n) {
                var r;
                if (!n)
                    return;
                return r = {
                    url: null,
                    cache: !0,
                    prepare: null,
                    replace: null,
                    wildcard: null,
                    limiter: null,
                    rateLimitBy: "debounce",
                    rateLimitWait: 300,
                    transform: t.identity,
                    transport: null
                },
                    n = t.isString(n) ? {
                        url: n
                    } : n,
                    n = t.mixin(r, n),
                !n.url && e.error("remote requires url to be set"),
                    n.transform = n.filter || n.transform,
                    n.prepare = s(n),
                    n.limiter = o(n),
                    n.transport = n.transport ? u(n.transport) : e.ajax,
                    delete n.replace,
                    delete n.wildcard,
                    delete n.rateLimitBy,
                    delete n.rateLimitWait,
                    n
            }
            function s(e) {
                function i(e, t) {
                    return t.url = n(t.url, e),
                        t
                }
                function s(e, t) {
                    return t.url = t.url.replace(r, encodeURIComponent(e)),
                        t
                }
                function o(e, t) {
                    return t
                }
                var t, n, r;
                return t = e.prepare,
                    n = e.replace,
                    r = e.wildcard,
                    t ? t : (n ? t = i : e.wildcard ? t = s : t = o,
                        t)
            }
            function o(e) {
                function s(e) {
                    return function(r) {
                        return t.debounce(r, e)
                    }
                }
                function o(e) {
                    return function(r) {
                        return t.throttle(r, e)
                    }
                }
                var n, r, i;
                return n = e.limiter,
                    r = e.rateLimitBy,
                    i = e.rateLimitWait,
                n || (n = /^throttle$/i.test(r) ? o(i) : s(i)),
                    n
            }
            function u(n) {
                return function(i) {
                    function o(e) {
                        t.defer(function() {
                            s.resolve(e)
                        })
                    }
                    function u(e) {
                        t.defer(function() {
                            s.reject(e)
                        })
                    }
                    var s = e.Deferred();
                    return n(i, o, u),
                        s
                }
            }
            return function(s) {
                var o, u;
                return o = {
                    initialize: !0,
                    identify: t.stringify,
                    datumTokenizer: null,
                    queryTokenizer: null,
                    sufficient: 5,
                    sorter: null,
                    local: [],
                    prefetch: null,
                    remote: null
                },
                    s = t.mixin(o, s || {}),
                !s.datumTokenizer && e.error("datumTokenizer is required"),
                !s.queryTokenizer && e.error("queryTokenizer is required"),
                    u = s.sorter,
                    s.sorter = u ? function(e) {
                        return e.sort(u)
                    }
                        : t.identity,
                    s.local = t.isFunction(s.local) ? s.local() : s.local,
                    s.prefetch = r(s.prefetch),
                    s.remote = i(s.remote),
                    s
            }
        }()
            , c = function() {
            function i(e) {
                e = l(e),
                    this.sorter = e.sorter,
                    this.identify = e.identify,
                    this.sufficient = e.sufficient,
                    this.local = e.local,
                    this.remote = e.remote ? new f(e.remote) : null,
                    this.prefetch = e.prefetch ? new a(e.prefetch) : null,
                    this.index = new u({
                        identify: this.identify,
                        datumTokenizer: e.datumTokenizer,
                        queryTokenizer: e.queryTokenizer
                    }),
                e.initialize !== !1 && this.initialize()
            }
            var n;
            return n = window && window.Bloodhound,
                i.noConflict = function() {
                    return window && (window.Bloodhound = n),
                        i
                }
                ,
                i.tokenizers = r,
                t.mixin(i.prototype, {
                    __ttAdapter: function() {
                        function n(e, n, r) {
                            return t.search(e, n, r)
                        }
                        function r(e, n) {
                            return t.search(e, n)
                        }
                        var t = this;
                        return this.remote ? n : r
                    },
                    _loadPrefetch: function() {
                        function s(e, t) {
                            if (e)
                                return r.reject();
                            n.add(t),
                                n.prefetch.store(n.index.serialize()),
                                r.resolve()
                        }
                        var n = this, r, i;
                        return r = e.Deferred(),
                            this.prefetch ? (i = this.prefetch.fromCache()) ? (this.index.bootstrap(i),
                                r.resolve()) : this.prefetch.fromNetwork(s) : r.resolve(),
                            r.promise()
                    },
                    _initialize: function() {
                        function r() {
                            t.add(t.local)
                        }
                        var t = this, n;
                        return this.clear(),
                            (this.initPromise = this._loadPrefetch()).done(r),
                            this.initPromise
                    },
                    initialize: function(t) {
                        return !this.initPromise || t ? this._initialize() : this.initPromise
                    },
                    add: function(t) {
                        return this.index.add(t),
                            this
                    },
                    get: function(n) {
                        return n = t.isArray(n) ? n : [].slice.call(arguments),
                            this.index.get(n)
                    },
                    search: function(n, r, i) {
                        function u(e) {
                            var n = [];
                            t.each(e, function(e) {
                                !t.some(o, function(t) {
                                    return s.identify(e) === s.identify(t)
                                }) && n.push(e)
                            }),
                            i && i(n)
                        }
                        var s = this, o;
                        return o = this.sorter(this.index.search(n)),
                            r(this.remote ? o.slice() : o),
                            this.remote && o.length < this.sufficient ? this.remote.get(n, u) : this.remote && this.remote.cancelLastRequest(),
                            this
                    },
                    all: function() {
                        return this.index.all()
                    },
                    clear: function() {
                        return this.index.reset(),
                            this
                    },
                    clearPrefetchCache: function() {
                        return this.prefetch && this.prefetch.clear(),
                            this
                    },
                    clearRemoteCache: function() {
                        return o.resetCache(),
                            this
                    },
                    ttAdapter: function() {
                        return this.__ttAdapter()
                    }
                }),
                i
        }();
        return c
    }),
    define("bdStatisticsEvents", [], function() {
        var e = [{
            eventId: "Q0001",
            category: "首页",
            eventDesc: "查询类别切换-全部"
        }, {
            eventId: "Q0002",
            category: "首页",
            eventDesc: "查询类别切换-法人或股东"
        }, {
            eventId: "Q0003",
            category: "首页",
            eventDesc: "查询类别切换-董监高"
        }, {
            eventId: "Q0004",
            category: "首页",
            eventDesc: "查询类别切换-品牌或产品"
        }, {
            eventId: "Q0005",
            category: "首页",
            eventDesc: "查询类别切换-联系方式"
        }, {
            eventId: "Q0006",
            category: "首页",
            eventDesc: "查询类别切换-经营范围"
        }, {
            eventId: "Q0007",
            category: "首页",
            eventDesc: "搜索企业"
        }, {
            eventId: "Q0008",
            category: "首页",
            eventDesc: "换一批"
        }, {
            eventId: "Q0009",
            category: "首页",
            eventDesc: "热搜词搜索"
        }, {
            eventId: "Q0010",
            category: "首页",
            eventDesc: "全国企业查询"
        }, {
            eventId: "Q0011",
            category: "首页",
            eventDesc: "最近更新企业"
        }, {
            eventId: "Q0012",
            category: "首页",
            eventDesc: "最新注册企业"
        }, {
            eventId: "Q0013",
            category: "首页",
            eventDesc: "行业查询"
        }, {
            eventId: "Q0014",
            category: "首页",
            eventDesc: "微信关注"
        }, {
            eventId: "Q0015",
            category: "首页",
            eventDesc: "app下载"
        }, {
            eventId: "Q0016",
            category: "搜索类别",
            eventDesc: "类别切换-企业"
        }, {
            eventId: "Q0017",
            category: "搜索类别",
            eventDesc: "类别切换-风险信息"
        }, {
            eventId: "Q0018",
            category: "搜索类别",
            eventDesc: "类别切换-知识产权"
        }, {
            eventId: "Q0019",
            category: "企业结果页",
            eventDesc: "查询列表-查看企业详情"
        }, {
            eventId: "Q0020",
            category: "企业结果页",
            eventDesc: "查询列表-关注企业"
        }, {
            eventId: "Q0021",
            category: "企业结果页",
            eventDesc: "查询列表-取消关注"
        }, {
            eventId: "Q0022",
            category: "企业结果页",
            eventDesc: "查询列表-分页查看"
        }, {
            eventId: "Q0023",
            category: "风险结果页",
            eventDesc: "风险-全部类别"
        }, {
            eventId: "Q0024",
            category: "风险结果页",
            eventDesc: "风险-法院公告"
        }, {
            eventId: "Q0025",
            category: "风险结果页",
            eventDesc: "风险-法院判决"
        }, {
            eventId: "Q0026",
            category: "风险结果页",
            eventDesc: "风险-失信人"
        }, {
            eventId: "Q0027",
            category: "风险结果页",
            eventDesc: "风险-被执行人"
        }, {
            eventId: "Q0028",
            category: "风险结果页",
            eventDesc: "风险-司法拍卖"
        }, {
            eventId: "Q0029",
            category: "风险结果页",
            eventDesc: "风险-经营异常"
        }, {
            eventId: "Q0030",
            category: "风险结果页",
            eventDesc: "风险-法院公告详情"
        }, {
            eventId: "Q0031",
            category: "风险结果页",
            eventDesc: "风险-法院判决详情"
        }, {
            eventId: "Q0032",
            category: "风险结果页",
            eventDesc: "风险-失信人详情"
        }, {
            eventId: "Q0033",
            category: "风险结果页",
            eventDesc: "风险-司法拍卖详情"
        }, {
            eventId: "Q0034",
            category: "风险结果页",
            eventDesc: "风险-时间过滤"
        }, {
            eventId: "Q0035",
            category: "风险结果页",
            eventDesc: "风险-分页查看"
        }, {
            eventId: "Q0036",
            category: "风险结果页",
            eventDesc: "风险-相关公司"
        }, {
            eventId: "Q0037",
            category: "知识产权页",
            eventDesc: "知识产权-全部类别"
        }, {
            eventId: "Q0038",
            category: "知识产权页",
            eventDesc: "知识产权-商标"
        }, {
            eventId: "Q0039",
            category: "知识产权页",
            eventDesc: "知识产权-专利"
        }, {
            eventId: "Q0040",
            category: "知识产权页",
            eventDesc: "知识产权-著作权"
        }, {
            eventId: "Q0041",
            category: "知识产权页",
            eventDesc: "知识产权-软件著作权"
        }, {
            eventId: "Q0042",
            category: "知识产权页",
            eventDesc: "知识产权-时间过滤"
        }, {
            eventId: "Q0043",
            category: "知识产权页",
            eventDesc: "知识产权-分页查看"
        }, {
            eventId: "Q0044",
            category: "知识产权页",
            eventDesc: "知识产权-专利详细"
        }, {
            eventId: "Q0045",
            category: "知识产权页",
            eventDesc: "知识产权-商标申请人详细"
        }, {
            eventId: "Q0046",
            category: "知识产权页",
            eventDesc: "知识产权-专利申请人详细"
        }, {
            eventId: "Q0047",
            category: "知识产权页",
            eventDesc: "知识产权-著作权申请人详细"
        }, {
            eventId: "Q0048",
            category: "知识产权页",
            eventDesc: "知识产权-软件著作权申请人详细"
        }, {
            eventId: "Q0049",
            category: "企业详情页",
            eventDesc: "公司详情-查看地图"
        }, {
            eventId: "Q0050",
            category: "企业详情页",
            eventDesc: "公司详情-关注企业"
        }, {
            eventId: "Q0051",
            category: "企业详情页",
            eventDesc: "公司详情-取消关注"
        }, {
            eventId: "Q0052",
            category: "企业详情页",
            eventDesc: "公司详情-在线更新"
        }, {
            eventId: "Q0053",
            category: "企业详情页",
            eventDesc: "公司详情-发送报告"
        }, {
            eventId: "Q0054",
            category: "企业详情页",
            eventDesc: "公司详情-分享"
        }, {
            eventId: "Q0055",
            category: "企业详情页",
            eventDesc: "公司详情-企业链图"
        }, {
            eventId: "Q0056",
            category: "企业详情页",
            eventDesc: "公司详情-工商变更图标"
        }, {
            eventId: "Q0057",
            category: "企业详情页",
            eventDesc: "公司详情-法院判决图标"
        }, {
            eventId: "Q0058",
            category: "企业详情页",
            eventDesc: "公司详情-法院公告图标"
        }, {
            eventId: "Q0059",
            category: "企业详情页",
            eventDesc: "公司详情-被执行人图标"
        }, {
            eventId: "Q0060",
            category: "企业详情页",
            eventDesc: "公司详情-失信人信息图标"
        }, {
            eventId: "Q0061",
            category: "企业详情页",
            eventDesc: "公司详情-经营异常图标"
        }, {
            eventId: "Q0062",
            category: "企业详情页",
            eventDesc: "公司详情-司法拍卖图标"
        }, {
            eventId: "Q0063",
            category: "企业详情页",
            eventDesc: "公司详情-企业基本信息"
        }, {
            eventId: "Q0064",
            category: "企业详情页",
            eventDesc: "公司详情-企业风险信息"
        }, {
            eventId: "Q0065",
            category: "企业详情页",
            eventDesc: "公司详情-企业知识产权"
        }, {
            eventId: "Q0066",
            category: "企业详情页",
            eventDesc: "公司详情-企业对外投资"
        }, {
            eventId: "Q0067",
            category: "企业详情页",
            eventDesc: "公司详情-企业年报"
        }, {
            eventId: "Q0068",
            category: "企业详情页",
            eventDesc: "公司详情-企业关联族谱"
        }, {
            eventId: "Q0069",
            category: "企业详情页",
            eventDesc: "公司详情-企业招聘"
        }, {
            eventId: "Q0070",
            category: "企业详情页",
            eventDesc: "公司详情-基本信息-点击法人代表"
        }, {
            eventId: "Q0071",
            category: "企业详情页",
            eventDesc: "公司详情-风险信息-查看工商变更详细"
        }, {
            eventId: "Q0072",
            category: "企业详情页",
            eventDesc: "公司详情-法院判决-查看详细"
        }, {
            eventId: "Q0073",
            category: "企业详情页",
            eventDesc: "公司详情-法院判决-阅读原文"
        }, {
            eventId: "Q0074",
            category: "企业详情页",
            eventDesc: "公司详情-法院判决-分页"
        }, {
            eventId: "Q0075",
            category: "企业详情页",
            eventDesc: "公司详情-法院公告-查看详细"
        }, {
            eventId: "Q0076",
            category: "企业详情页",
            eventDesc: "公司详情-被执行人-分页查看"
        }, {
            eventId: "Q0077",
            category: "企业详情页",
            eventDesc: "公司详情-失信人-分页查看"
        }, {
            eventId: "Q0078",
            category: "企业详情页",
            eventDesc: "公司详情-司法拍卖-分页查看"
        }, {
            eventId: "Q0079",
            category: "企业详情页",
            eventDesc: "公司详情-经营异常-分页查看"
        }, {
            eventId: "Q0080",
            category: "企业详情页",
            eventDesc: "公司详情-专利信息-查看专利详细"
        }, {
            eventId: "Q0081",
            category: "企业详情页",
            eventDesc: "公司详情-专利信息-阅读原文"
        }, {
            eventId: "Q0082",
            category: "企业详情页",
            eventDesc: "公司详情-专利信息-分页查看"
        }, {
            eventId: "Q0083",
            category: "企业详情页",
            eventDesc: "公司详情-著作权-分页查看"
        }, {
            eventId: "Q0084",
            category: "企业详情页",
            eventDesc: "公司详情-软件著作权-分页查看"
        }, {
            eventId: "Q0085",
            category: "企业详情页",
            eventDesc: "公司详情-域名-分页查看"
        }, {
            eventId: "Q0086",
            category: "企业详情页",
            eventDesc: "公司详情-对外投资-点击查看公司详细"
        }, {
            eventId: "Q0087",
            category: "企业详情页",
            eventDesc: "公司详情-对外投资-分页查看"
        }, {
            eventId: "Q0088",
            category: "企业详情页",
            eventDesc: "公司详情-企业年报-年报年份标签切换"
        }, {
            eventId: "Q0089",
            category: "企业详情页",
            eventDesc: "公司详情-商标信息-分页查看"
        }, {
            eventId: "Q0090",
            category: "企业详情页",
            eventDesc: "公司详情-企业动态-加载更多"
        }, {
            eventId: "Q0092",
            category: "企业详情页",
            eventDesc: "公司详情-展开风险概览"
        }, {
            eventId: "Q0093",
            category: "企业详情页",
            eventDesc: "公司详情-收起风险概览"
        }, {
            eventId: "Q0094",
            category: "企业详情页",
            eventDesc: "公司详情-评分规则"
        }, {
            eventId: "Q0091",
            category: "搜索",
            eventDesc: "搜索-顶部搜索框"
        }, {
            eventId: "Q0100",
            category: "用户帮助",
            eventDesc: "用户帮助-关于启信宝"
        }, {
            eventId: "Q0101",
            category: "用户帮助",
            eventDesc: "用户帮助-意见反馈"
        }, {
            eventId: "Q0102",
            category: "用户帮助",
            eventDesc: "用户帮助-常见问题"
        }, {
            eventId: "Q0103",
            category: "用户帮助",
            eventDesc: "用户帮助-服务协议"
        }, {
            eventId: "Q0104",
            category: "用户帮助",
            eventDesc: "用户帮助-旧版入口"
        }, {
            eventId: "Q0105",
            category: "个人中心",
            eventDesc: "个人中心-消息中心"
        }, {
            eventId: "Q0106",
            category: "个人中心",
            eventDesc: "个人中心-个人信息"
        }, {
            eventId: "Q0107",
            category: "个人中心",
            eventDesc: "个人中心-账号设置"
        }, {
            eventId: "Q0108",
            category: "个人中心",
            eventDesc: "个人中心-关注企业"
        }, {
            eventId: "Q0109",
            category: "个人中心",
            eventDesc: "个人中心-浏览历史"
        }, {
            eventId: "Q0110",
            category: "个人中心",
            eventDesc: "个人中心-消息中心打开企业详情"
        }, {
            eventId: "Q0111",
            category: "个人中心",
            eventDesc: "个人中心-关注企业打开企业详情"
        }, {
            eventId: "Q0112",
            category: "个人中心",
            eventDesc: "个人中心-浏览历史打开企业详情"
        }, {
            eventId: "Q0113",
            category: "个人中心",
            eventDesc: "个人中心-修改个人信息"
        }, {
            eventId: "Q0114",
            category: "个人中心",
            eventDesc: "个人中心-修改账号密码"
        }, {
            eventId: "Q0115",
            category: "个人中心",
            eventDesc: "个人中心-关注企业分页查看"
        }, {
            eventId: "Q0116",
            category: "个人中心",
            eventDesc: "个人中心-消息中心分页查看"
        }, {
            eventId: "Q0117",
            category: "个人中心",
            eventDesc: "个人中心-消息中心全选"
        }, {
            eventId: "Q0118",
            category: "个人中心",
            eventDesc: "个人中心-消息中心删除"
        }, {
            eventId: "Q0119",
            category: "个人中心",
            eventDesc: "个人中心-消息中心标记已读"
        }, {
            eventId: "Q0120",
            category: "个人中心",
            eventDesc: "个人中心-浏览历史分页查看"
        }, {
            eventId: "Q0121",
            category: "浏览历史",
            eventDesc: "浏览历史-打开企业详细"
        }, {
            eventId: "Q0122",
            category: "相关企业",
            eventDesc: "相关企业-打开企业详细"
        }, {
            eventId: "Q0123",
            category: "登录注册",
            eventDesc: "首页-用户登录"
        }, {
            eventId: "Q0124",
            category: "登录注册",
            eventDesc: "首页-用户注册"
        }, {
            eventId: "Q0125",
            category: "登录注册",
            eventDesc: "登录注册-用户退出"
        }, {
            eventId: "Q0126",
            category: "登录注册",
            eventDesc: "企业结果页-关注登录"
        }, {
            eventId: "Q0127",
            category: "登录注册",
            eventDesc: "企业结果页-底部登录"
        }, {
            eventId: "Q0128",
            category: "登录注册",
            eventDesc: "风险结果页-类别切换登录"
        }, {
            eventId: "Q0129",
            category: "登录注册",
            eventDesc: "风险结果页-底部登录"
        }, {
            eventId: "Q0130",
            category: "登录注册",
            eventDesc: "知识产权结果页-类别切换登录"
        }, {
            eventId: "Q0131",
            category: "登录注册",
            eventDesc: "知识产权结果页-底部登录"
        }, {
            eventId: "Q0132",
            category: "登录注册",
            eventDesc: "企业详情页-基本信息登录"
        }, {
            eventId: "Q0133",
            category: "登录注册",
            eventDesc: "企业详情页-风险图标登录"
        }, {
            eventId: "Q0134",
            category: "登录注册",
            eventDesc: "企业详情页-类别切换登录"
        }, {
            eventId: "Q0135",
            category: "登录注册",
            eventDesc: "企业详情页-查看图谱登录"
        }, {
            eventId: "Q0136",
            category: "登录注册",
            eventDesc: "企业详情页-查看动态登录"
        }, {
            eventId: "Q0137",
            category: "登录注册",
            eventDesc: "企业详情页-关注登录"
        }, {
            eventId: "Q0138",
            category: "登录注册",
            eventDesc: "企业详情页-发送报告登录"
        }, {
            eventId: "Q0201",
            category: "二次筛选",
            eventDesc: "二次筛选-打开二次筛选"
        }, {
            eventId: "Q0202",
            category: "二次筛选",
            eventDesc: "二次筛选-排序"
        }, {
            eventId: "Q0203",
            category: "二次筛选",
            eventDesc: "二次筛选-搜索范围"
        }, {
            eventId: "Q0204",
            category: "二次筛选",
            eventDesc: "二次筛选-注册资本"
        }, {
            eventId: "Q0205",
            category: "二次筛选",
            eventDesc: "二次筛选-成立年限"
        }, {
            eventId: "Q0206",
            category: "二次筛选",
            eventDesc: "二次筛选-区域"
        }, {
            eventId: "Q0207",
            category: "二次筛选",
            eventDesc: "二次筛选-行业"
        }, {
            eventId: "Q0208",
            category: "二次筛选",
            eventDesc: "二次筛选-清除筛选"
        }, {
            eventId: "Q0209",
            category: "二次筛选",
            eventDesc: "二次筛选-收起筛选"
        }, {
            eventId: "Q0401",
            category: "关联族谱",
            eventDesc: "关联族谱-打开其他公司关联族谱"
        }, {
            eventId: "Q0402",
            category: "关联族谱",
            eventDesc: "关联族谱-登录查看点击"
        }, {
            eventId: "Q0403",
            category: "关联族谱",
            eventDesc: "关联族谱-查看样例点击"
        }, {
            eventId: "Q0139",
            category: "搜索",
            eventDesc: "热搜词顶部搜索"
        }, {
            eventId: "Q0140",
            category: "搜索",
            eventDesc: "行业顶部搜索"
        }, {
            eventId: "Q0141",
            category: "搜索",
            eventDesc: "区域顶部搜索"
        }, {
            eventId: "Q0142",
            category: "搜索",
            eventDesc: "企业顶部搜索"
        }, {
            eventId: "Q0143",
            category: "搜索",
            eventDesc: "风险顶部搜索"
        }, {
            eventId: "Q0144",
            category: "搜索",
            eventDesc: "知识产权顶部搜索"
        }, {
            eventId: "Q0145",
            category: "搜索",
            eventDesc: "企业链图顶部搜索"
        }, {
            eventId: "Q0146",
            category: "搜索",
            eventDesc: "企业族谱顶部搜索"
        }, {
            eventId: "Q0147",
            category: "搜索",
            eventDesc: "企业详情顶部搜索"
        }, {
            eventId: "Q0148",
            category: "搜索",
            eventDesc: "用户协议顶部搜索"
        }, {
            eventId: "Q0149",
            category: "搜索",
            eventDesc: "常见问题顶部搜索"
        }, {
            eventId: "Q0150",
            category: "搜索",
            eventDesc: "关于启信宝顶部搜索"
        }, {
            eventId: "QS001",
            category: "搜索",
            eventDesc: "筛选收起"
        }, {
            eventId: "QS002",
            category: "搜索",
            eventDesc: "筛选展开"
        }, {
            eventId: "Q0151",
            category: "首页",
            eventDesc: "首页-工商查询"
        }, {
            eventId: "Q0152",
            category: "首页",
            eventDesc: "首页-商标查询"
        }, {
            eventId: "Q0153",
            category: "首页",
            eventDesc: "首页-专利查询"
        }, {
            eventId: "Q0154",
            category: "首页",
            eventDesc: "首页-失信查询"
        }, {
            eventId: "Q0155",
            category: "首页",
            eventDesc: "首页-立即体验"
        }, {
            eventId: "Q0156",
            category: "首页",
            eventDesc: "首页-推荐阅读"
        }, {
            eventId: "Q0157",
            category: "阅读",
            eventDesc: "阅读-详细-热门文章"
        }, {
            eventId: "Q0158",
            category: "阅读",
            eventDesc: "阅读-详细-分享到新浪"
        }, {
            eventId: "Q0159",
            category: "阅读",
            eventDesc: "阅读-详细-分享到QQ空间"
        }, {
            eventId: "Q0160",
            category: "阅读",
            eventDesc: "阅读-详细-分享到QQ好友"
        }, {
            eventId: "Q0161",
            category: "阅读",
            eventDesc: "阅读-详细-分享到微信"
        }, {
            eventId: "Q0162",
            category: "页底",
            eventDesc: "点击数据来源"
        }, {
            eventId: "Q0163",
            category: "页底",
            eventDesc: "点击公司备案"
        }, {
            eventId: "Q0164",
            category: "页底",
            eventDesc: "友情链接"
        }, {
            eventId: "Q0165",
            category: "个人中心",
            eventDesc: "个人中心-积分中心"
        }, {
            eventId: "Q0166",
            category: "个人中心",
            eventDesc: "个人中心-我的订单"
        }, {
            eventId: "Q0167",
            category: "购买增值版报告",
            eventDesc: "购买增值版报告-展开增值版报告详情"
        }, {
            eventId: "Q0168",
            category: "购买增值版报告",
            eventDesc: "购买增值版报告-收起增值版报告详情"
        }, {
            eventId: "Q0169",
            category: "购买增值版报告",
            eventDesc: "购买增值版报告-点击购买（直接点击购买）"
        }, {
            eventId: "Q0170",
            category: "购买增值版报告",
            eventDesc: "购买增值版报告-点击购买（基本信息-法人对外投资）"
        }, {
            eventId: "Q0171",
            category: "购买增值版报告",
            eventDesc: "购买增值版报告-点击购买（对外投资-法人对外投资）"
        }, {
            eventId: "Q0172",
            category: "绑定手机",
            eventDesc: "绑定手机（第三方登录）"
        }, {
            eventId: "Q0173",
            category: "个人中心",
            eventDesc: "个人中心-会员中心"
        }, {
            eventId: "Q0174",
            category: "会员介绍",
            eventDesc: "会员介绍"
        }, {
            eventId: "Q0175",
            category: "升级VIP",
            eventDesc: "查询列表页-升级VIP（上）"
        }, {
            eventId: "Q0176",
            category: "升级VIP",
            eventDesc: "查询列表页-升级VIP（下）"
        }, {
            eventId: "Q0177",
            category: "添加监控工商变更",
            eventDesc: "添加监控工商变更"
        }, {
            eventId: "Q0178",
            category: "用户帮助",
            eventDesc: "用户帮助-公司名录"
        }, {
            eventId: "Q0179",
            category: "用户帮助",
            eventDesc: "用户帮助-品牌列表"
        }, {
            eventId: "Q0301",
            category: "深度搜索",
            eventDesc: "深度搜索无返回结果切换区域"
        }, {
            eventId: "Q0302",
            category: "深度搜索",
            eventDesc: "深度搜索无返回结果直接反馈"
        }, {
            eventId: "Q0303",
            category: "深度搜索",
            eventDesc: "搜索页无搜索结果搜索更多"
        }, {
            eventId: "Q0304",
            category: "深度搜索",
            eventDesc: "搜索结果小于10条搜索更多"
        }];
        return e
    }),
    define("notification", ["jquery", "toastr"], function(e, t) {
        return t.options.closeButton = !0,
            t.options.timeOut = 5e3,
            t.options.extendedTimeOut = 5e3,
            t.options.progressBar = !0,
            t.options.positionClass = "toast-top-center",
        {
            init: function() {},
            hide: function() {},
            success: function(e, n) {
                n = n || "成功",
                    t.success(e, n)
            },
            error: function(e, n) {
                n = n || "错误",
                    t.error(e, n)
            },
            info: function(e, n) {
                n = n || "提醒",
                    t.info(e, n)
            },
            warning: function(e, n) {
                n = n || "警告",
                    t.warning(e, n)
            }
        }
    }),
    define("limitwords", ["require", "exports", "module"], function(e, t, n) {
        function o(e) {
            var t = new RegExp("[+\"\\%\\[\\]-`~!@#$^&*()=|{}':;',\\\\.<>/?~！@#￥……&*（）_—|{}《》【】‘’；：”“'。，、？]")
                , n = "";
            for (var r = 0; r < e.length; r++)
                n += e.substr(r, 1).replace(t, "");
            return n
        }
        function u(e) {
            var t = e.replace(/(^\s+)|(\s+$)/g, "").replace(/\s/g, "");
            for (var n = 0; n < r.length; n++) {
                var u = r[n]
                    , a = new RegExp(u + "市","g")
                    , f = new RegExp(u,"g");
                t = t.replace(a, "").replace(f, "")
            }
            for (var n = 0; n < i.length; n++) {
                var u = i[n]
                    , a = new RegExp(u + "省","g")
                    , f = new RegExp(u,"g");
                t = t.replace(a, "").replace(f, "")
            }
            for (var n = 0; n < s.length; n++) {
                var u = s[n]
                    , l = new RegExp(u,"g");
                t = t.replace(l, "")
            }
            return t = o(t),
            t.length > 0
        }
        var r = ["苏州", "无锡", "南京", "镇江", "南通", "扬州", "宿迁", "徐州", "淮安", "连云港", "常州", "泰州", "盐城", "深圳", "广州", "惠州", "梅州", "汕头", "珠海", "佛山", "肇庆", "湛江", "江门", "河源", "清远", "云浮", "东莞", "中山", "阳江", "揭阳", "茂名", "汕尾", "韶关", "潮州", "北京", "天津", "上海", "重庆", "石家庄", "张家口", "秦皇岛", "承德", "唐山", "沧州", "衡水", "邢台", "邯郸", "保定", "廊坊", "郑州", "新乡", "许昌", "信阳", "南阳", "开封", "洛阳", "商丘", "焦作", "鹤壁", "濮阳", "周口", "漯河", "济源", "安阳", "平顶山", "驻马店", "三门峡", "合肥", "芜湖", "淮南", "安庆", "宿州", "阜阳", "亳州", "黄山", "滁州", "淮北", "铜陵", "宣城", "六安", "巢湖", "池州", "蚌埠", "马鞍山", "杭州", "舟山", "湖州", "嘉兴", "金华", "绍兴", "台州", "温州", "丽水", "衢州", "宁波", "福州", "泉州", "漳州", "龙岩", "南平", "厦门", "宁德", "莆田", "三明", "兰州", "平凉", "庆阳", "武威", "金昌", "酒泉", "天水", "陇南", "临夏", "合作", "白银", "定西", "张掖", "嘉峪关", "南宁", "柳州", "来宾", "桂林", "梧州", "防城港", "贵港", "玉林", "百色", "钦州", "河池", "北海", "崇左", "贺州", "贵阳", "安顺", "都匀", "兴义", "铜仁", "毕节", "六盘水", "遵义", "凯里", "昆明", "红河", "文山", "玉溪", "楚雄", "普洱", "昭通", "临沧", "怒江", "香格里拉", "丽江", "德宏", "景洪", "大理", "曲靖", "保山", "呼和浩特", "乌海", "集宁", "通辽", "阿拉善左旗", "鄂尔多斯", "临河", "锡林浩特", "呼伦贝尔", "乌兰浩特", "包头", "赤峰", "南昌", "上饶", "抚州", "宜春", "鹰潭", "赣州", "景德镇", "萍乡", "新余", "九江", "吉安", "武汉", "荆州", "黄冈", "宜昌", "恩施", "十堰", "神农架", "随州", "荆门", "天门", "仙桃", "潜江", "襄阳", "鄂州", "孝感", "黄石", "咸宁", "成都", "自贡", "绵阳", "南充", "达州", "遂宁", "广安", "巴中", "泸州", "宜宾", "内江", "资阳", "乐山", "眉山", "凉山", "雅安", "甘孜", "阿坝", "德阳", "广元", "攀枝花", "银川", "中卫", "固原", "石嘴山", "吴忠", "西宁", "黄南", "海北", "果洛", "玉树", "海西", "海东", "海南", "济南", "潍坊", "临沂", "菏泽", "滨州", "东营", "威海", "枣庄", "日照", "莱芜", "聊城", "青岛", "淄博", "德州", "烟台", "济宁", "泰安", "西安", "延安", "榆林", "铜川", "商洛", "安康", "汉中", "宝鸡", "咸阳", "渭南", "太原", "临汾", "运城", "朔州", "忻州", "长治", "大同", "阳泉", "晋中", "晋城", "吕梁", "乌鲁木齐", "石河子", "图木舒克", "五家渠", "昌吉", "吐鲁番", "库尔勒", "阿拉尔", "阿克苏", "喀什", "伊宁", "塔城", "哈密", "和田", "阿勒泰", "阿图什", "博乐", "克拉玛依", "拉萨", "山南", "昌都", "那曲", "日喀则", "林芝", "海口", "三亚", "东方", "临高", "澄迈", "儋州", "昌江", "白沙", "琼中", "定安", "屯昌", "琼海", "文昌", "保亭", "万宁", "陵水", "乐东", "五指山", "长沙", "株洲", "衡阳", "郴州", "常德", "益阳", "娄底", "邵阳", "岳阳", "张家界", "怀化", "永州", "吉首", "湘潭", "哈尔滨", "牡丹江", "佳木斯", "绥化", "黑河", "双鸭山", "伊春", "大庆", "七台河", "鸡西", "鹤岗", "齐齐哈尔", "大兴安岭", "长春", "延吉", "四平", "白山", "白城", "辽源", "松原", "吉林", "通化", "沈阳", "鞍山", "抚顺", "本溪", "丹东", "葫芦岛", "营口", "阜新", "辽阳", "铁岭", "朝阳", "盘锦", "大连", "锦州"]
            , i = ["北京", "上海", "天津", "重庆", "广东", "江苏", "山东", "浙江", "河北", "河南", "辽宁", "四川", "湖北", "湖南", "福建", "安徽", "内蒙古", "陕西", "江西", "广西", "黑龙江", "吉林", "云南", "山西", "新疆", "贵州", "甘肃", "海南", "宁夏", "青海", "西藏"]
            , s = ["公司", "有限", "有限公司", "限公", "科技", "发展"];
        n.exports = {
            parse: u
        }
    }),
    function(e, t) {
        typeof define == "function" && define.amd ? define("typeahead", ["jquery"], function(e) {
            return t(e)
        }) : typeof exports == "object" ? module.exports = t(require("jquery")) : t(jQuery)
    }(this, function(e) {
        var t = function() {
            return {
                isMsie: function() {
                    return /(msie|trident)/i.test(navigator.userAgent) ? navigator.userAgent.match(/(msie |rv:)(\d+(.\d+)?)/i)[2] : !1
                },
                isBlankString: function(e) {
                    return !e || /^\s*$/.test(e)
                },
                escapeRegExChars: function(e) {
                    return e.replace(/[\-\[\]\/\{\}\(\)\*\+\?\.\\\^\$\|]/g, "\\$&")
                },
                isString: function(e) {
                    return typeof e == "string"
                },
                isNumber: function(e) {
                    return typeof e == "number"
                },
                isArray: e.isArray,
                isFunction: e.isFunction,
                isObject: e.isPlainObject,
                isUndefined: function(e) {
                    return typeof e == "undefined"
                },
                isElement: function(e) {
                    return !!e && e.nodeType === 1
                },
                isJQuery: function(t) {
                    return t instanceof e
                },
                toStr: function(n) {
                    return t.isUndefined(n) || n === null ? "" : n + ""
                },
                bind: e.proxy,
                each: function(t, n) {
                    function r(e, t) {
                        return n(t, e)
                    }
                    e.each(t, r)
                },
                map: e.map,
                filter: e.grep,
                every: function(t, n) {
                    var r = !0;
                    return t ? (e.each(t, function(e, i) {
                        if (!(r = n.call(null, i, e, t)))
                            return !1
                    }),
                        !!r) : r
                },
                some: function(t, n) {
                    var r = !1;
                    return t ? (e.each(t, function(e, i) {
                        if (r = n.call(null, i, e, t))
                            return !1
                    }),
                        !!r) : r
                },
                mixin: e.extend,
                identity: function(e) {
                    return e
                },
                clone: function(t) {
                    return e.extend(!0, {}, t)
                },
                getIdGenerator: function() {
                    var e = 0;
                    return function() {
                        return e++
                    }
                },
                templatify: function(n) {
                    function r() {
                        return String(n)
                    }
                    return e.isFunction(n) ? n : r
                },
                defer: function(e) {
                    setTimeout(e, 0)
                },
                debounce: function(e, t, n) {
                    var r, i;
                    return function() {
                        var s = this, o = arguments, u, a;
                        return u = function() {
                            r = null,
                            n || (i = e.apply(s, o))
                        }
                            ,
                            a = n && !r,
                            clearTimeout(r),
                            r = setTimeout(u, t),
                        a && (i = e.apply(s, o)),
                            i
                    }
                },
                throttle: function(e, t) {
                    var n, r, i, s, o, u;
                    return o = 0,
                        u = function() {
                            o = new Date,
                                i = null,
                                s = e.apply(n, r)
                        }
                        ,
                        function() {
                            var a = new Date
                                , f = t - (a - o);
                            return n = this,
                                r = arguments,
                                f <= 0 ? (clearTimeout(i),
                                    i = null,
                                    o = a,
                                    s = e.apply(n, r)) : i || (i = setTimeout(u, f)),
                                s
                        }
                },
                stringify: function(e) {
                    return t.isString(e) ? e : JSON.stringify(e)
                },
                noop: function() {}
            }
        }()
            , n = function() {
            function n(n) {
                var o, u;
                return u = t.mixin({}, e, n),
                    o = {
                        css: s(),
                        classes: u,
                        html: r(u),
                        selectors: i(u)
                    },
                {
                    css: o.css,
                    html: o.html,
                    classes: o.classes,
                    selectors: o.selectors,
                    mixin: function(e) {
                        t.mixin(e, o)
                    }
                }
            }
            function r(e) {
                return {
                    wrapper: '<span class="' + e.wrapper + '"></span>',
                    menu: '<div class="' + e.menu + '"></div>'
                }
            }
            function i(e) {
                var n = {};
                return t.each(e, function(e, t) {
                    n[t] = "." + e
                }),
                    n
            }
            function s() {
                var e = {
                    wrapper: {
                        position: "relative",
                        display: "inline-block"
                    },
                    hint: {
                        position: "absolute",
                        top: "0",
                        left: "0",
                        borderColor: "transparent",
                        boxShadow: "none",
                        opacity: "1"
                    },
                    input: {
                        position: "relative",
                        verticalAlign: "top",
                        backgroundColor: "transparent"
                    },
                    inputWithNoHint: {
                        position: "relative",
                        verticalAlign: "top"
                    },
                    menu: {
                        position: "absolute",
                        top: "100%",
                        left: "0",
                        zIndex: "100",
                        display: "none"
                    },
                    ltr: {
                        left: "0",
                        right: "auto"
                    },
                    rtl: {
                        left: "auto",
                        right: " 0"
                    }
                };
                return t.isMsie() && t.mixin(e.input, {
                    backgroundImage: "url(data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7)"
                }),
                    e
            }
            var e = {
                wrapper: "twitter-typeahead",
                input: "tt-input",
                hint: "tt-hint",
                menu: "tt-menu",
                dataset: "tt-dataset",
                suggestion: "tt-suggestion",
                selectable: "tt-selectable",
                empty: "tt-empty",
                open: "tt-open",
                cursor: "tt-cursor",
                highlight: "tt-highlight"
            };
            return n
        }()
            , r = function() {
            function i(t) {
                (!t || !t.el) && e.error("EventBus initialized without el"),
                    this.$el = e(t.el)
            }
            var n, r;
            return n = "typeahead:",
                r = {
                    render: "rendered",
                    cursorchange: "cursorchanged",
                    select: "selected",
                    autocomplete: "autocompleted"
                },
                t.mixin(i.prototype, {
                    _trigger: function(t, r) {
                        var i;
                        return i = e.Event(n + t),
                            (r = r || []).unshift(i),
                            this.$el.trigger.apply(this.$el, r),
                            i
                    },
                    before: function(e) {
                        var t, n;
                        return t = [].slice.call(arguments, 1),
                            n = this._trigger("before" + e, t),
                            n.isDefaultPrevented()
                    },
                    trigger: function(e) {
                        var t;
                        this._trigger(e, [].slice.call(arguments, 1)),
                        (t = r[e]) && this._trigger(t, [].slice.call(arguments, 1))
                    }
                }),
                i
        }()
            , i = function() {
            function n(t, n, r, i) {
                var s;
                if (!r)
                    return this;
                n = n.split(e),
                    r = i ? f(r, i) : r,
                    this._callbacks = this._callbacks || {};
                while (s = n.shift())
                    this._callbacks[s] = this._callbacks[s] || {
                            sync: [],
                            async: []
                        },
                        this._callbacks[s][t].push(r);
                return this
            }
            function r(e, t, r) {
                return n.call(this, "async", e, t, r)
            }
            function i(e, t, r) {
                return n.call(this, "sync", e, t, r)
            }
            function s(t) {
                var n;
                if (!this._callbacks)
                    return this;
                t = t.split(e);
                while (n = t.shift())
                    delete this._callbacks[n];
                return this
            }
            function o(n) {
                var r, i, s, o, a;
                if (!this._callbacks)
                    return this;
                n = n.split(e),
                    s = [].slice.call(arguments, 1);
                while ((r = n.shift()) && (i = this._callbacks[r]))
                    o = u(i.sync, this, [r].concat(s)),
                        a = u(i.async, this, [r].concat(s)),
                    o() && t(a);
                return this
            }
            function u(e, t, n) {
                function r() {
                    var r;
                    for (var i = 0, s = e.length; !r && i < s; i += 1)
                        r = e[i].apply(t, n) === !1;
                    return !r
                }
                return r
            }
            function a() {
                var e;
                return window.setImmediate ? e = function(t) {
                    setImmediate(function() {
                        t()
                    })
                }
                    : e = function(t) {
                    setTimeout(function() {
                        t()
                    }, 0)
                }
                    ,
                    e
            }
            function f(e, t) {
                return e.bind ? e.bind(t) : function() {
                    e.apply(t, [].slice.call(arguments, 0))
                }
            }
            var e = /\s+/
                , t = a();
            return {
                onSync: i,
                onAsync: r,
                off: s,
                trigger: o
            }
        }()
            , s = function(e) {
            function r(e, n, r) {
                var i = [], s;
                for (var o = 0, u = e.length; o < u; o++)
                    i.push(t.escapeRegExChars(e[o]));
                return s = r ? "\\b(" + i.join("|") + ")\\b" : "(" + i.join("|") + ")",
                    n ? new RegExp(s) : new RegExp(s,"i")
            }
            var n = {
                node: null,
                pattern: null,
                tagName: "strong",
                className: null,
                wordsOnly: !1,
                caseSensitive: !1
            };
            return function(s) {
                function u(t) {
                    var n, r, i;
                    if (n = o.exec(t.data))
                        i = e.createElement(s.tagName),
                        s.className && (i.className = s.className),
                            r = t.splitText(n.index),
                            r.splitText(n[0].length),
                            i.appendChild(r.cloneNode(!0)),
                            t.parentNode.replaceChild(i, r);
                    return !!n
                }
                function a(e, t) {
                    var n, r = 3;
                    for (var i = 0; i < e.childNodes.length; i++)
                        n = e.childNodes[i],
                            n.nodeType === r ? i += t(n) ? 1 : 0 : a(n, t)
                }
                var o;
                s = t.mixin({}, n, s);
                if (!s.node || !s.pattern)
                    return;
                s.pattern = t.isArray(s.pattern) ? s.pattern : [s.pattern],
                    o = r(s.pattern, s.caseSensitive, s.wordsOnly),
                    a(s.node, u)
            }
        }(window.document)
            , o = function() {
            function r(n, r) {
                n = n || {},
                n.input || e.error("input is missing"),
                    r.mixin(this),
                    this.$hint = e(n.hint),
                    this.$input = e(n.input),
                    this.query = this.$input.val(),
                    this.queryWhenFocused = this.hasFocus() ? this.query : null,
                    this.$overflowHelper = s(this.$input),
                    this._checkLanguageDirection(),
                this.$hint.length === 0 && (this.setHint = this.getHint = this.clearHint = this.clearHintIfInvalid = t.noop)
            }
            function s(t) {
                return e('<pre aria-hidden="true"></pre>').css({
                    position: "absolute",
                    visibility: "hidden",
                    whiteSpace: "pre",
                    fontFamily: t.css("font-family"),
                    fontSize: t.css("font-size"),
                    fontStyle: t.css("font-style"),
                    fontVariant: t.css("font-variant"),
                    fontWeight: t.css("font-weight"),
                    wordSpacing: t.css("word-spacing"),
                    letterSpacing: t.css("letter-spacing"),
                    textIndent: t.css("text-indent"),
                    textRendering: t.css("text-rendering"),
                    textTransform: t.css("text-transform")
                }).insertAfter(t)
            }
            function o(e, t) {
                return r.normalizeQuery(e) === r.normalizeQuery(t)
            }
            function u(e) {
                return e.altKey || e.ctrlKey || e.metaKey || e.shiftKey
            }
            var n;
            return n = {
                9: "tab",
                27: "esc",
                37: "left",
                39: "right",
                13: "enter",
                38: "up",
                40: "down"
            },
                r.normalizeQuery = function(e) {
                    return t.toStr(e).replace(/^\s*/g, "").replace(/\s{2,}/g, " ")
                }
                ,
                t.mixin(r.prototype, i, {
                    _onBlur: function() {
                        this.resetInputValue(),
                            this.trigger("blurred")
                    },
                    _onFocus: function() {
                        this.queryWhenFocused = this.query,
                            this.trigger("focused")
                    },
                    _onKeydown: function(t) {
                        var r = n[t.which || t.keyCode];
                        this._managePreventDefault(r, t),
                        r && this._shouldTrigger(r, t) && this.trigger(r + "Keyed", t)
                    },
                    _onInput: function() {
                        this._setQuery(this.getInputValue()),
                            this.clearHintIfInvalid(),
                            this._checkLanguageDirection()
                    },
                    _managePreventDefault: function(t, n) {
                        var r;
                        switch (t) {
                            case "up":
                            case "down":
                                r = !u(n);
                                break;
                            default:
                                r = !1
                        }
                        r && n.preventDefault()
                    },
                    _shouldTrigger: function(t, n) {
                        var r;
                        switch (t) {
                            case "tab":
                                r = !u(n);
                                break;
                            default:
                                r = !0
                        }
                        return r
                    },
                    _checkLanguageDirection: function() {
                        var t = (this.$input.css("direction") || "ltr").toLowerCase();
                        this.dir !== t && (this.dir = t,
                            this.$hint.attr("dir", t),
                            this.trigger("langDirChanged", t))
                    },
                    _setQuery: function(t, n) {
                        var r, i;
                        r = o(t, this.query),
                            i = r ? this.query.length !== t.length : !1,
                            this.query = t,
                            !n && !r ? this.trigger("queryChanged", this.query) : !n && i && this.trigger("whitespaceChanged", this.query)
                    },
                    bind: function() {
                        var e = this, r, i, s, o;
                        return r = t.bind(this._onBlur, this),
                            i = t.bind(this._onFocus, this),
                            s = t.bind(this._onKeydown, this),
                            o = t.bind(this._onInput, this),
                            this.$input.on("blur.tt", r).on("focus.tt", i).on("keydown.tt", s),
                            !t.isMsie() || t.isMsie() > 9 ? this.$input.on("input.tt", o) : this.$input.on("keydown.tt keypress.tt cut.tt paste.tt", function(r) {
                                if (n[r.which || r.keyCode])
                                    return;
                                t.defer(t.bind(e._onInput, e, r))
                            }),
                            this
                    },
                    focus: function() {
                        this.$input.focus()
                    },
                    blur: function() {
                        this.$input.blur()
                    },
                    getLangDir: function() {
                        return this.dir
                    },
                    getQuery: function() {
                        return this.query || ""
                    },
                    setQuery: function(t, n) {
                        this.setInputValue(t),
                            this._setQuery(t, n)
                    },
                    hasQueryChangedSinceLastFocus: function() {
                        return this.query !== this.queryWhenFocused
                    },
                    getInputValue: function() {
                        return this.$input.val()
                    },
                    setInputValue: function(t) {
                        this.$input.val(t),
                            this.clearHintIfInvalid(),
                            this._checkLanguageDirection()
                    },
                    resetInputValue: function() {
                        this.setInputValue(this.query)
                    },
                    getHint: function() {
                        return this.$hint.val()
                    },
                    setHint: function(t) {
                        this.$hint.val(t)
                    },
                    clearHint: function() {
                        this.setHint("")
                    },
                    clearHintIfInvalid: function() {
                        var t, n, r, i;
                        t = this.getInputValue(),
                            n = this.getHint(),
                            r = t !== n && n.indexOf(t) === 0,
                            i = t !== "" && r && !this.hasOverflow(),
                        !i && this.clearHint()
                    },
                    hasFocus: function() {
                        return this.$input.is(":focus")
                    },
                    hasOverflow: function() {
                        var t = this.$input.width() - 2;
                        return this.$overflowHelper.text(this.getInputValue()),
                        this.$overflowHelper.width() >= t
                    },
                    isCursorAtEnd: function() {
                        var e, n, r;
                        return e = this.$input.val().length,
                            n = this.$input[0].selectionStart,
                            t.isNumber(n) ? n === e : document.selection ? (r = document.selection.createRange(),
                                r.moveStart("character", -e),
                            e === r.text.length) : !0
                    },
                    destroy: function() {
                        this.$hint.off(".tt"),
                            this.$input.off(".tt"),
                            this.$overflowHelper.remove(),
                            this.$hint = this.$input = this.$overflowHelper = e("<div>")
                    }
                }),
                r
        }()
            , u = function() {
            function o(n, i) {
                n = n || {},
                    n.templates = n.templates || {},
                    n.templates.notFound = n.templates.notFound || n.templates.empty,
                n.source || e.error("missing source"),
                n.node || e.error("missing node"),
                n.name && !f(n.name) && e.error("invalid dataset name: " + n.name),
                    i.mixin(this),
                    this.highlight = !!n.highlight,
                    this.name = n.name || r(),
                    this.limit = n.limit || 5,
                    this.displayFn = u(n.display || n.displayKey),
                    this.templates = a(n.templates, this.displayFn),
                    this.source = n.source.__ttAdapter ? n.source.__ttAdapter() : n.source,
                    this.async = t.isUndefined(n.async) ? this.source.length > 2 : !!n.async,
                    this._resetLastSuggestion(),
                    this.$el = e(n.node).addClass(this.classes.dataset).addClass(this.classes.dataset + "-" + this.name)
            }
            function u(e) {
                function n(t) {
                    return t[e]
                }
                return e = e || t.stringify,
                    t.isFunction(e) ? e : n
            }
            function a(n, r) {
                function i(t) {
                    return e("<div>").text(r(t))
                }
                return {
                    notFound: n.notFound && t.templatify(n.notFound),
                    pending: n.pending && t.templatify(n.pending),
                    header: n.header && t.templatify(n.header),
                    footer: n.footer && t.templatify(n.footer),
                    suggestion: n.suggestion || i
                }
            }
            function f(e) {
                return /^[_a-zA-Z0-9-]+$/.test(e)
            }
            var n, r;
            return n = {
                val: "tt-selectable-display",
                obj: "tt-selectable-object"
            },
                r = t.getIdGenerator(),
                o.extractData = function(r) {
                    var i = e(r);
                    return i.data(n.obj) ? {
                        val: i.data(n.val) || "",
                        obj: i.data(n.obj) || null
                    } : null
                }
                ,
                t.mixin(o.prototype, i, {
                    _overwrite: function(t, n) {
                        n = n || [],
                            n.length ? this._renderSuggestions(t, n) : this.async && this.templates.pending ? this._renderPending(t) : !this.async && this.templates.notFound ? this._renderNotFound(t) : this._empty(),
                            this.trigger("rendered", this.name, n, !1)
                    },
                    _append: function(t, n) {
                        n = n || [],
                            n.length && this.$lastSuggestion.length ? this._appendSuggestions(t, n) : n.length ? this._renderSuggestions(t, n) : !this.$lastSuggestion.length && this.templates.notFound && this._renderNotFound(t),
                            this.trigger("rendered", this.name, n, !0)
                    },
                    _renderSuggestions: function(t, n) {
                        var r;
                        r = this._getSuggestionsFragment(t, n),
                            this.$lastSuggestion = r.children().last(),
                            this.$el.html(r).prepend(this._getHeader(t, n)).append(this._getFooter(t, n))
                    },
                    _appendSuggestions: function(t, n) {
                        var r, i;
                        r = this._getSuggestionsFragment(t, n),
                            i = r.children().last(),
                            this.$lastSuggestion.after(r),
                            this.$lastSuggestion = i
                    },
                    _renderPending: function(t) {
                        var n = this.templates.pending;
                        this._resetLastSuggestion(),
                        n && this.$el.html(n({
                            query: t,
                            dataset: this.name
                        }))
                    },
                    _renderNotFound: function(t) {
                        var n = this.templates.notFound;
                        this._resetLastSuggestion(),
                        n && this.$el.html(n({
                            query: t,
                            dataset: this.name
                        }))
                    },
                    _empty: function() {
                        this.$el.empty(),
                            this._resetLastSuggestion()
                    },
                    _getSuggestionsFragment: function(i, o) {
                        var u = this, a;
                        return a = document.createDocumentFragment(),
                            t.each(o, function(r) {
                                var s, o;
                                o = u._injectQuery(i, r),
                                    s = e(u.templates.suggestion(o)).data(n.obj, r).data(n.val, u.displayFn(r)).addClass(u.classes.suggestion + " " + u.classes.selectable),
                                    a.appendChild(s[0])
                            }),
                        this.highlight && s({
                            className: this.classes.highlight,
                            node: a,
                            pattern: i
                        }),
                            e(a)
                    },
                    _getFooter: function(t, n) {
                        return this.templates.footer ? this.templates.footer({
                            query: t,
                            suggestions: n,
                            dataset: this.name
                        }) : null
                    },
                    _getHeader: function(t, n) {
                        return this.templates.header ? this.templates.header({
                            query: t,
                            suggestions: n,
                            dataset: this.name
                        }) : null
                    },
                    _resetLastSuggestion: function() {
                        this.$lastSuggestion = e()
                    },
                    _injectQuery: function(n, r) {
                        return t.isObject(r) ? t.mixin({
                            _query: n
                        }, r) : r
                    },
                    update: function(n) {
                        function u(e) {
                            if (s)
                                return;
                            s = !0,
                                e = (e || []).slice(0, r.limit),
                                o = e.length,
                                r._overwrite(n, e),
                            o < r.limit && r.async && r.trigger("asyncRequested", n)
                        }
                        function a(t) {
                            t = t || [],
                            !i && o < r.limit && (r.cancel = e.noop,
                                r._append(n, t.slice(0, r.limit - o)),
                                o += t.length,
                            r.async && r.trigger("asyncReceived", n))
                        }
                        var r = this
                            , i = !1
                            , s = !1
                            , o = 0;
                        this.cancel(),
                            this.cancel = function() {
                                i = !0,
                                    r.cancel = e.noop,
                                r.async && r.trigger("asyncCanceled", n)
                            }
                            ,
                            this.source(n, u, a),
                        !s && u([])
                    },
                    cancel: e.noop,
                    clear: function() {
                        this._empty(),
                            this.cancel(),
                            this.trigger("cleared")
                    },
                    isEmpty: function() {
                        return this.$el.is(":empty")
                    },
                    destroy: function() {
                        this.$el = e("<div>")
                    }
                }),
                o
        }()
            , a = function() {
            function n(n, r) {
                function s(t) {
                    var n = i.$node.find(t.node).first();
                    return t.node = n.length ? n : e("<div>").appendTo(i.$node),
                        new u(t,r)
                }
                var i = this;
                n = n || {},
                n.node || e.error("node is required"),
                    r.mixin(this),
                    this.$node = e(n.node),
                    this.query = null,
                    this.datasets = t.map(n.datasets, s)
            }
            return t.mixin(n.prototype, i, {
                _onSelectableClick: function(n) {
                    this.trigger("selectableClicked", e(n.currentTarget))
                },
                _onRendered: function(t, n, r, i) {
                    this.$node.toggleClass(this.classes.empty, this._allDatasetsEmpty()),
                        this.trigger("datasetRendered", n, r, i)
                },
                _onCleared: function() {
                    this.$node.toggleClass(this.classes.empty, this._allDatasetsEmpty()),
                        this.trigger("datasetCleared")
                },
                _propagate: function() {
                    this.trigger.apply(this, arguments)
                },
                _allDatasetsEmpty: function() {
                    function n(e) {
                        return e.isEmpty()
                    }
                    return t.every(this.datasets, n)
                },
                _getSelectables: function() {
                    return this.$node.find(this.selectors.selectable)
                },
                _removeCursor: function() {
                    var t = this.getActiveSelectable();
                    t && t.removeClass(this.classes.cursor)
                },
                _ensureVisible: function(t) {
                    var n, r, i, s;
                    n = t.position().top,
                        r = n + t.outerHeight(!0),
                        i = this.$node.scrollTop(),
                        s = this.$node.height() + parseInt(this.$node.css("paddingTop"), 10) + parseInt(this.$node.css("paddingBottom"), 10),
                        n < 0 ? this.$node.scrollTop(i + n) : s < r && this.$node.scrollTop(i + (r - s))
                },
                bind: function() {
                    var e = this, n;
                    return n = t.bind(this._onSelectableClick, this),
                        this.$node.on("click.tt", this.selectors.selectable, n),
                        t.each(this.datasets, function(t) {
                            t.onSync("asyncRequested", e._propagate, e).onSync("asyncCanceled", e._propagate, e).onSync("asyncReceived", e._propagate, e).onSync("rendered", e._onRendered, e).onSync("cleared", e._onCleared, e)
                        }),
                        this
                },
                isOpen: function() {
                    return this.$node.hasClass(this.classes.open)
                },
                open: function() {
                    this.$node.addClass(this.classes.open)
                },
                close: function() {
                    this.$node.removeClass(this.classes.open),
                        this._removeCursor()
                },
                setLanguageDirection: function(t) {
                    this.$node.attr("dir", t)
                },
                selectableRelativeToCursor: function(t) {
                    var n, r, i, s;
                    return r = this.getActiveSelectable(),
                        n = this._getSelectables(),
                        i = r ? n.index(r) : -1,
                        s = i + t,
                        s = (s + 1) % (n.length + 1) - 1,
                        s = s < -1 ? n.length - 1 : s,
                        s === -1 ? null : n.eq(s)
                },
                setCursor: function(t) {
                    this._removeCursor();
                    if (t = t && t.first())
                        t.addClass(this.classes.cursor),
                            this._ensureVisible(t)
                },
                getSelectableData: function(t) {
                    return t && t.length ? u.extractData(t) : null
                },
                getActiveSelectable: function() {
                    var t = this._getSelectables().filter(this.selectors.cursor).first();
                    return t.length ? t : null
                },
                getTopSelectable: function() {
                    var t = this._getSelectables().first();
                    return t.length ? t : null
                },
                update: function(n) {
                    function i(e) {
                        e.update(n)
                    }
                    var r = n !== this.query;
                    return r && (this.query = n,
                        t.each(this.datasets, i)),
                        r
                },
                empty: function() {
                    function n(e) {
                        e.clear()
                    }
                    t.each(this.datasets, n),
                        this.query = null,
                        this.$node.addClass(this.classes.empty)
                },
                destroy: function() {
                    function r(e) {
                        e.destroy()
                    }
                    this.$node.off(".tt"),
                        this.$node = e("<div>"),
                        t.each(this.datasets, r)
                }
            }),
                n
        }()
            , f = function() {
            function n() {
                a.apply(this, [].slice.call(arguments, 0))
            }
            var e = a.prototype;
            return t.mixin(n.prototype, a.prototype, {
                open: function() {
                    return !this._allDatasetsEmpty() && this._show(),
                        e.open.apply(this, [].slice.call(arguments, 0))
                },
                close: function() {
                    return this._hide(),
                        e.close.apply(this, [].slice.call(arguments, 0))
                },
                _onRendered: function() {
                    return this._allDatasetsEmpty() ? this._hide() : this.isOpen() && this._show(),
                        e._onRendered.apply(this, [].slice.call(arguments, 0))
                },
                _onCleared: function() {
                    return this._allDatasetsEmpty() ? this._hide() : this.isOpen() && this._show(),
                        e._onCleared.apply(this, [].slice.call(arguments, 0))
                },
                setLanguageDirection: function(n) {
                    return this.$node.css(n === "ltr" ? this.css.ltr : this.css.rtl),
                        e.setLanguageDirection.apply(this, [].slice.call(arguments, 0))
                },
                _hide: function() {
                    this.$node.hide()
                },
                _show: function() {
                    this.$node.css("display", "block")
                }
            }),
                n
        }()
            , l = function() {
            function n(n, i) {
                var s, o, u, a, f, l, h, p, d, v, m;
                n = n || {},
                n.input || e.error("missing input"),
                n.menu || e.error("missing menu"),
                n.eventBus || e.error("missing event bus"),
                    i.mixin(this),
                    this.eventBus = n.eventBus,
                    this.minLength = t.isNumber(n.minLength) ? n.minLength : 1,
                    this.input = n.input,
                    this.menu = n.menu,
                    this.enabled = !0,
                    this.active = !1,
                this.input.hasFocus() && this.activate(),
                    this.dir = this.input.getLangDir(),
                    this._hacks(),
                    this.menu.bind().onSync("selectableClicked", this._onSelectableClicked, this).onSync("asyncRequested", this._onAsyncRequested, this).onSync("asyncCanceled", this._onAsyncCanceled, this).onSync("asyncReceived", this._onAsyncReceived, this).onSync("datasetRendered", this._onDatasetRendered, this).onSync("datasetCleared", this._onDatasetCleared, this),
                    s = r(this, "activate", "open", "_onFocused"),
                    o = r(this, "deactivate", "_onBlurred"),
                    u = r(this, "isActive", "isOpen", "_onEnterKeyed"),
                    a = r(this, "isActive", "isOpen", "_onTabKeyed"),
                    f = r(this, "isActive", "_onEscKeyed"),
                    l = r(this, "isActive", "open", "_onUpKeyed"),
                    h = r(this, "isActive", "open", "_onDownKeyed"),
                    p = r(this, "isActive", "isOpen", "_onLeftKeyed"),
                    d = r(this, "isActive", "isOpen", "_onRightKeyed"),
                    v = r(this, "_openIfActive", "_onQueryChanged"),
                    m = r(this, "_openIfActive", "_onWhitespaceChanged"),
                    this.input.bind().onSync("focused", s, this).onSync("blurred", o, this).onSync("enterKeyed", u, this).onSync("tabKeyed", a, this).onSync("escKeyed", f, this).onSync("upKeyed", l, this).onSync("downKeyed", h, this).onSync("leftKeyed", p, this).onSync("rightKeyed", d, this).onSync("queryChanged", v, this).onSync("whitespaceChanged", m, this).onSync("langDirChanged", this._onLangDirChanged, this)
            }
            function r(e) {
                var n = [].slice.call(arguments, 1);
                return function() {
                    var r = [].slice.call(arguments);
                    t.each(n, function(t) {
                        return e[t].apply(e, r)
                    })
                }
            }
            return t.mixin(n.prototype, {
                _hacks: function() {
                    var r, i;
                    r = this.input.$input || e("<div>"),
                        i = this.menu.$node || e("<div>"),
                        r.on("blur.tt", function(e) {
                            var n, s, o;
                            n = document.activeElement,
                                s = i.is(n),
                                o = i.has(n).length > 0,
                            t.isMsie() && (s || o) && (e.preventDefault(),
                                e.stopImmediatePropagation(),
                                t.defer(function() {
                                    r.focus()
                                }))
                        }),
                        i.on("mousedown.tt", function(e) {
                            e.preventDefault()
                        })
                },
                _onSelectableClicked: function(t, n) {
                    this.select(n)
                },
                _onDatasetCleared: function() {
                    this._updateHint()
                },
                _onDatasetRendered: function(t, n, r, i) {
                    this._updateHint(),
                        this.eventBus.trigger("render", r, i, n)
                },
                _onAsyncRequested: function(t, n, r) {
                    this.eventBus.trigger("asyncrequest", r, n)
                },
                _onAsyncCanceled: function(t, n, r) {
                    this.eventBus.trigger("asynccancel", r, n)
                },
                _onAsyncReceived: function(t, n, r) {
                    this.eventBus.trigger("asyncreceive", r, n)
                },
                _onFocused: function() {
                    this._minLengthMet() && this.menu.update(this.input.getQuery())
                },
                _onBlurred: function() {
                    this.input.hasQueryChangedSinceLastFocus() && this.eventBus.trigger("change", this.input.getQuery())
                },
                _onEnterKeyed: function(t, n) {
                    var r;
                    (r = this.menu.getActiveSelectable()) && this.select(r) && n.preventDefault()
                },
                _onTabKeyed: function(t, n) {
                    var r;
                    (r = this.menu.getActiveSelectable()) ? this.select(r) && n.preventDefault() : (r = this.menu.getTopSelectable()) && this.autocomplete(r) && n.preventDefault()
                },
                _onEscKeyed: function() {
                    this.close()
                },
                _onUpKeyed: function() {
                    this.moveCursor(-1)
                },
                _onDownKeyed: function() {
                    this.moveCursor(1)
                },
                _onLeftKeyed: function() {
                    this.dir === "rtl" && this.input.isCursorAtEnd() && this.autocomplete(this.menu.getTopSelectable())
                },
                _onRightKeyed: function() {
                    this.dir === "ltr" && this.input.isCursorAtEnd() && this.autocomplete(this.menu.getTopSelectable())
                },
                _onQueryChanged: function(t, n) {
                    this._minLengthMet(n) ? this.menu.update(n) : this.menu.empty()
                },
                _onWhitespaceChanged: function() {
                    this._updateHint()
                },
                _onLangDirChanged: function(t, n) {
                    this.dir !== n && (this.dir = n,
                        this.menu.setLanguageDirection(n))
                },
                _openIfActive: function() {
                    this.isActive() && this.open()
                },
                _minLengthMet: function(n) {
                    return n = t.isString(n) ? n : this.input.getQuery() || "",
                    n.length >= this.minLength
                },
                _updateHint: function() {
                    var n, r, i, s, u, a, f;
                    n = this.menu.getTopSelectable(),
                        r = this.menu.getSelectableData(n),
                        i = this.input.getInputValue(),
                        r && !t.isBlankString(i) && !this.input.hasOverflow() ? (s = o.normalizeQuery(i),
                            u = t.escapeRegExChars(s),
                            a = new RegExp("^(?:" + u + ")(.+$)","i"),
                            f = a.exec(r.val),
                        f && this.input.setHint(i + f[1])) : this.input.clearHint()
                },
                isEnabled: function() {
                    return this.enabled
                },
                enable: function() {
                    this.enabled = !0
                },
                disable: function() {
                    this.enabled = !1
                },
                isActive: function() {
                    return this.active
                },
                activate: function() {
                    return this.isActive() ? !0 : !this.isEnabled() || this.eventBus.before("active") ? !1 : (this.active = !0,
                        this.eventBus.trigger("active"),
                        !0)
                },
                deactivate: function() {
                    return this.isActive() ? this.eventBus.before("idle") ? !1 : (this.active = !1,
                        this.close(),
                        this.eventBus.trigger("idle"),
                        !0) : !0
                },
                isOpen: function() {
                    return this.menu.isOpen()
                },
                open: function() {
                    return !this.isOpen() && !this.eventBus.before("open") && (this.menu.open(),
                        this._updateHint(),
                        this.eventBus.trigger("open")),
                        this.isOpen()
                },
                close: function() {
                    return this.isOpen() && !this.eventBus.before("close") && (this.menu.close(),
                        this.input.clearHint(),
                        this.input.resetInputValue(),
                        this.eventBus.trigger("close")),
                        !this.isOpen()
                },
                setVal: function(n) {
                    this.input.setQuery(t.toStr(n))
                },
                getVal: function() {
                    return this.input.getQuery()
                },
                select: function(t) {
                    var n = this.menu.getSelectableData(t);
                    return n && !this.eventBus.before("select", n.obj) ? (this.input.setQuery(n.val, !0),
                        this.eventBus.trigger("select", n.obj),
                        this.close(),
                        !0) : !1
                },
                autocomplete: function(t) {
                    var n, r, i;
                    return n = this.input.getQuery(),
                        r = this.menu.getSelectableData(t),
                        i = r && n !== r.val,
                        i && !this.eventBus.before("autocomplete", r.obj) ? (this.input.setQuery(r.val),
                            this.eventBus.trigger("autocomplete", r.obj),
                            !0) : !1
                },
                moveCursor: function(t) {
                    var n, r, i, s, o;
                    return n = this.input.getQuery(),
                        r = this.menu.selectableRelativeToCursor(t),
                        i = this.menu.getSelectableData(r),
                        s = i ? i.obj : null,
                        o = this._minLengthMet() && this.menu.update(n),
                        !o && !this.eventBus.before("cursorchange", s) ? (this.menu.setCursor(r),
                            i ? this.input.setInputValue(i.val) : (this.input.resetInputValue(),
                                this._updateHint()),
                            this.eventBus.trigger("cursorchange", s),
                            !0) : !1
                },
                destroy: function() {
                    this.input.destroy(),
                        this.menu.destroy()
                }
            }),
                n
        }();
        (function() {
            function c(t, n) {
                t.each(function() {
                    var t = e(this), r;
                    (r = t.data(s.typeahead)) && n(r, t)
                })
            }
            function h(e, t) {
                return e.clone().addClass(t.classes.hint).removeData().css(t.css.hint).css(d(e)).prop("readonly", !0).removeAttr("id name placeholder required").attr({
                    autocomplete: "off",
                    spellcheck: "false",
                    tabindex: -1
                })
            }
            function p(e, t) {
                e.data(s.attrs, {
                    dir: e.attr("dir"),
                    autocomplete: e.attr("autocomplete"),
                    spellcheck: e.attr("spellcheck"),
                    style: e.attr("style")
                }),
                    e.addClass(t.classes.input).attr({
                        autocomplete: "off",
                        spellcheck: !1
                    });
                try {
                    !e.attr("dir") && e.attr("dir", "auto")
                } catch (n) {}
                return e
            }
            function d(e) {
                return {
                    backgroundAttachment: e.css("background-attachment"),
                    backgroundClip: e.css("background-clip"),
                    backgroundColor: e.css("background-color"),
                    backgroundImage: e.css("background-image"),
                    backgroundOrigin: e.css("background-origin"),
                    backgroundPosition: e.css("background-position"),
                    backgroundRepeat: e.css("background-repeat"),
                    backgroundSize: e.css("background-size")
                }
            }
            function v(e) {
                var n, r;
                n = e.data(s.www),
                    r = e.parent().filter(n.selectors.wrapper),
                    t.each(e.data(s.attrs), function(n, r) {
                        t.isUndefined(n) ? e.removeAttr(r) : e.attr(r, n)
                    }),
                    e.removeData(s.typeahead).removeData(s.www).removeData(s.attr).removeClass(n.classes.input),
                r.length && (e.detach().insertAfter(r),
                    r.remove())
            }
            function m(n) {
                var r, i;
                return r = t.isJQuery(n) || t.isElement(n),
                    i = r ? e(n).first() : [],
                    i.length ? i : null
            }
            var i, s, u;
            i = e.fn.typeahead,
                s = {
                    www: "tt-www",
                    attrs: "tt-attrs",
                    typeahead: "tt-typeahead"
                },
                u = {
                    initialize: function(u, c) {
                        function v() {
                            var n, i, v, g, y, b, w, E, S, x, T;
                            t.each(c, function(e) {
                                e.highlight = !!u.highlight
                            }),
                                n = e(this),
                                i = e(d.html.wrapper),
                                v = m(u.hint),
                                g = m(u.menu),
                                y = u.hint !== !1 && !v,
                                b = u.menu !== !1 && !g,
                            y && (v = h(n, d)),
                            b && (g = e(d.html.menu).css(d.css.menu)),
                            v && v.val(""),
                                n = p(n, d);
                            if (y || b)
                                i.css(d.css.wrapper),
                                    n.css(y ? d.css.input : d.css.inputWithNoHint),
                                    n.wrap(i).parent().prepend(y ? v : null).append(b ? g : null);
                            T = b ? f : a,
                                w = new r({
                                    el: n
                                }),
                                E = new o({
                                    hint: v,
                                    input: n
                                },d),
                                S = new T({
                                    node: g,
                                    datasets: c
                                },d),
                                x = new l({
                                    input: E,
                                    menu: S,
                                    eventBus: w,
                                    minLength: u.minLength
                                },d),
                                n.data(s.www, d),
                                n.data(s.typeahead, x)
                        }
                        var d;
                        return c = t.isArray(c) ? c : [].slice.call(arguments, 1),
                            u = u || {},
                            d = n(u.classNames),
                            this.each(v)
                    },
                    isEnabled: function() {
                        var t;
                        return c(this.first(), function(e) {
                            t = e.isEnabled()
                        }),
                            t
                    },
                    enable: function() {
                        return c(this, function(e) {
                            e.enable()
                        }),
                            this
                    },
                    disable: function() {
                        return c(this, function(e) {
                            e.disable()
                        }),
                            this
                    },
                    isActive: function() {
                        var t;
                        return c(this.first(), function(e) {
                            t = e.isActive()
                        }),
                            t
                    },
                    activate: function() {
                        return c(this, function(e) {
                            e.activate()
                        }),
                            this
                    },
                    deactivate: function() {
                        return c(this, function(e) {
                            e.deactivate()
                        }),
                            this
                    },
                    isOpen: function() {
                        var t;
                        return c(this.first(), function(e) {
                            t = e.isOpen()
                        }),
                            t
                    },
                    open: function() {
                        return c(this, function(e) {
                            e.open()
                        }),
                            this
                    },
                    close: function() {
                        return c(this, function(e) {
                            e.close()
                        }),
                            this
                    },
                    select: function(n) {
                        var r = !1
                            , i = e(n);
                        return c(this.first(), function(e) {
                            r = e.select(i)
                        }),
                            r
                    },
                    autocomplete: function(n) {
                        var r = !1
                            , i = e(n);
                        return c(this.first(), function(e) {
                            r = e.autocomplete(i)
                        }),
                            r
                    },
                    moveCursor: function(t) {
                        var n = !1;
                        return c(this.first(), function(e) {
                            n = e.moveCursor(t)
                        }),
                            n
                    },
                    val: function(t) {
                        var n;
                        return arguments.length ? (c(this, function(e) {
                            e.setVal(t)
                        }),
                            this) : (c(this.first(), function(e) {
                            n = e.getVal()
                        }),
                            n)
                    },
                    destroy: function() {
                        return c(this, function(e, t) {
                            v(t),
                                e.destroy()
                        }),
                            this
                    }
                },
                e.fn.typeahead = function(e) {
                    return u[e] ? u[e].apply(this, [].slice.call(arguments, 1)) : u.initialize.apply(this, arguments)
                }
                ,
                e.fn.typeahead.noConflict = function() {
                    return e.fn.typeahead = i,
                        this
                }
        })()
    }),
    function(e) {
        typeof define == "function" && define.amd ? define("jquery-effect", ["jquery"], e) : e(jQuery)
    }(function(e) {
        var t = "ui-effects-"
            , n = e;
        e.effects = {
            effect: {}
        },
            function(e, t) {
                function h(e, t, n) {
                    var r = u[t.type] || {};
                    return e == null ? n || !t.def ? null : t.def : (e = r.floor ? ~~e : parseFloat(e),
                        isNaN(e) ? t.def : r.mod ? (e + r.mod) % r.mod : 0 > e ? 0 : r.max < e ? r.max : e)
                }
                function p(t) {
                    var n = s()
                        , r = n._rgba = [];
                    return t = t.toLowerCase(),
                        c(i, function(e, i) {
                            var s, u = i.re.exec(t), a = u && i.parse(u), f = i.space || "rgba";
                            if (a)
                                return s = n[f](a),
                                    n[o[f].cache] = s[o[f].cache],
                                    r = n._rgba = s._rgba,
                                    !1
                        }),
                        r.length ? (r.join() === "0,0,0,0" && e.extend(r, l.transparent),
                            n) : l[t]
                }
                function d(e, t, n) {
                    return n = (n + 1) % 1,
                        n * 6 < 1 ? e + (t - e) * n * 6 : n * 2 < 1 ? t : n * 3 < 2 ? e + (t - e) * (2 / 3 - n) * 6 : e
                }
                var n = "backgroundColor borderBottomColor borderLeftColor borderRightColor borderTopColor color columnRuleColor outlineColor textDecorationColor textEmphasisColor", r = /^([\-+])=\s*(\d+\.?\d*)/, i = [{
                    re: /rgba?\(\s*(\d{1,3})\s*,\s*(\d{1,3})\s*,\s*(\d{1,3})\s*(?:,\s*(\d?(?:\.\d+)?)\s*)?\)/,
                    parse: function(e) {
                        return [e[1], e[2], e[3], e[4]]
                    }
                }, {
                    re: /rgba?\(\s*(\d+(?:\.\d+)?)\%\s*,\s*(\d+(?:\.\d+)?)\%\s*,\s*(\d+(?:\.\d+)?)\%\s*(?:,\s*(\d?(?:\.\d+)?)\s*)?\)/,
                    parse: function(e) {
                        return [e[1] * 2.55, e[2] * 2.55, e[3] * 2.55, e[4]]
                    }
                }, {
                    re: /#([a-f0-9]{2})([a-f0-9]{2})([a-f0-9]{2})/,
                    parse: function(e) {
                        return [parseInt(e[1], 16), parseInt(e[2], 16), parseInt(e[3], 16)]
                    }
                }, {
                    re: /#([a-f0-9])([a-f0-9])([a-f0-9])/,
                    parse: function(e) {
                        return [parseInt(e[1] + e[1], 16), parseInt(e[2] + e[2], 16), parseInt(e[3] + e[3], 16)]
                    }
                }, {
                    re: /hsla?\(\s*(\d+(?:\.\d+)?)\s*,\s*(\d+(?:\.\d+)?)\%\s*,\s*(\d+(?:\.\d+)?)\%\s*(?:,\s*(\d?(?:\.\d+)?)\s*)?\)/,
                    space: "hsla",
                    parse: function(e) {
                        return [e[1], e[2] / 100, e[3] / 100, e[4]]
                    }
                }], s = e.Color = function(t, n, r, i) {
                    return new e.Color.fn.parse(t,n,r,i)
                }
                    , o = {
                    rgba: {
                        props: {
                            red: {
                                idx: 0,
                                type: "byte"
                            },
                            green: {
                                idx: 1,
                                type: "byte"
                            },
                            blue: {
                                idx: 2,
                                type: "byte"
                            }
                        }
                    },
                    hsla: {
                        props: {
                            hue: {
                                idx: 0,
                                type: "degrees"
                            },
                            saturation: {
                                idx: 1,
                                type: "percent"
                            },
                            lightness: {
                                idx: 2,
                                type: "percent"
                            }
                        }
                    }
                }, u = {
                    "byte": {
                        floor: !0,
                        max: 255
                    },
                    percent: {
                        max: 1
                    },
                    degrees: {
                        mod: 360,
                        floor: !0
                    }
                }, a = s.support = {}, f = e("<p>")[0], l, c = e.each;
                f.style.cssText = "background-color:rgba(1,1,1,.5)",
                    a.rgba = f.style.backgroundColor.indexOf("rgba") > -1,
                    c(o, function(e, t) {
                        t.cache = "_" + e,
                            t.props.alpha = {
                                idx: 3,
                                type: "percent",
                                def: 1
                            }
                    }),
                    s.fn = e.extend(s.prototype, {
                        parse: function(n, r, i, u) {
                            if (n === t)
                                return this._rgba = [null, null, null, null],
                                    this;
                            if (n.jquery || n.nodeType)
                                n = e(n).css(r),
                                    r = t;
                            var a = this
                                , f = e.type(n)
                                , d = this._rgba = [];
                            r !== t && (n = [n, r, i, u],
                                f = "array");
                            if (f === "string")
                                return this.parse(p(n) || l._default);
                            if (f === "array")
                                return c(o.rgba.props, function(e, t) {
                                    d[t.idx] = h(n[t.idx], t)
                                }),
                                    this;
                            if (f === "object")
                                return n instanceof s ? c(o, function(e, t) {
                                    n[t.cache] && (a[t.cache] = n[t.cache].slice())
                                }) : c(o, function(t, r) {
                                    var i = r.cache;
                                    c(r.props, function(e, t) {
                                        if (!a[i] && r.to) {
                                            if (e === "alpha" || n[e] == null)
                                                return;
                                            a[i] = r.to(a._rgba)
                                        }
                                        a[i][t.idx] = h(n[e], t, !0)
                                    }),
                                    a[i] && e.inArray(null, a[i].slice(0, 3)) < 0 && (a[i][3] = 1,
                                    r.from && (a._rgba = r.from(a[i])))
                                }),
                                    this
                        },
                        is: function(e) {
                            var t = s(e)
                                , n = !0
                                , r = this;
                            return c(o, function(e, i) {
                                var s, o = t[i.cache];
                                return o && (s = r[i.cache] || i.to && i.to(r._rgba) || [],
                                    c(i.props, function(e, t) {
                                        if (o[t.idx] != null)
                                            return n = o[t.idx] === s[t.idx],
                                                n
                                    })),
                                    n
                            }),
                                n
                        },
                        _space: function() {
                            var e = []
                                , t = this;
                            return c(o, function(n, r) {
                                t[r.cache] && e.push(n)
                            }),
                                e.pop()
                        },
                        transition: function(e, t) {
                            var n = s(e)
                                , r = n._space()
                                , i = o[r]
                                , a = this.alpha() === 0 ? s("transparent") : this
                                , f = a[i.cache] || i.to(a._rgba)
                                , l = f.slice();
                            return n = n[i.cache],
                                c(i.props, function(e, r) {
                                    var i = r.idx
                                        , s = f[i]
                                        , o = n[i]
                                        , a = u[r.type] || {};
                                    if (o === null)
                                        return;
                                    s === null ? l[i] = o : (a.mod && (o - s > a.mod / 2 ? s += a.mod : s - o > a.mod / 2 && (s -= a.mod)),
                                        l[i] = h((o - s) * t + s, r))
                                }),
                                this[r](l)
                        },
                        blend: function(t) {
                            if (this._rgba[3] === 1)
                                return this;
                            var n = this._rgba.slice()
                                , r = n.pop()
                                , i = s(t)._rgba;
                            return s(e.map(n, function(e, t) {
                                return (1 - r) * i[t] + r * e
                            }))
                        },
                        toRgbaString: function() {
                            var t = "rgba("
                                , n = e.map(this._rgba, function(e, t) {
                                return e == null ? t > 2 ? 1 : 0 : e
                            });
                            return n[3] === 1 && (n.pop(),
                                t = "rgb("),
                            t + n.join() + ")"
                        },
                        toHslaString: function() {
                            var t = "hsla("
                                , n = e.map(this.hsla(), function(e, t) {
                                return e == null && (e = t > 2 ? 1 : 0),
                                t && t < 3 && (e = Math.round(e * 100) + "%"),
                                    e
                            });
                            return n[3] === 1 && (n.pop(),
                                t = "hsl("),
                            t + n.join() + ")"
                        },
                        toHexString: function(t) {
                            var n = this._rgba.slice()
                                , r = n.pop();
                            return t && n.push(~~(r * 255)),
                            "#" + e.map(n, function(e) {
                                return e = (e || 0).toString(16),
                                    e.length === 1 ? "0" + e : e
                            }).join("")
                        },
                        toString: function() {
                            return this._rgba[3] === 0 ? "transparent" : this.toRgbaString()
                        }
                    }),
                    s.fn.parse.prototype = s.fn,
                    o.hsla.to = function(e) {
                        if (e[0] == null || e[1] == null || e[2] == null)
                            return [null, null, null, e[3]];
                        var t = e[0] / 255, n = e[1] / 255, r = e[2] / 255, i = e[3], s = Math.max(t, n, r), o = Math.min(t, n, r), u = s - o, a = s + o, f = a * .5, l, c;
                        return o === s ? l = 0 : t === s ? l = 60 * (n - r) / u + 360 : n === s ? l = 60 * (r - t) / u + 120 : l = 60 * (t - n) / u + 240,
                            u === 0 ? c = 0 : f <= .5 ? c = u / a : c = u / (2 - a),
                            [Math.round(l) % 360, c, f, i == null ? 1 : i]
                    }
                    ,
                    o.hsla.from = function(e) {
                        if (e[0] == null || e[1] == null || e[2] == null)
                            return [null, null, null, e[3]];
                        var t = e[0] / 360
                            , n = e[1]
                            , r = e[2]
                            , i = e[3]
                            , s = r <= .5 ? r * (1 + n) : r + n - r * n
                            , o = 2 * r - s;
                        return [Math.round(d(o, s, t + 1 / 3) * 255), Math.round(d(o, s, t) * 255), Math.round(d(o, s, t - 1 / 3) * 255), i]
                    }
                    ,
                    c(o, function(n, i) {
                        var o = i.props
                            , u = i.cache
                            , a = i.to
                            , f = i.from;
                        s.fn[n] = function(n) {
                            a && !this[u] && (this[u] = a(this._rgba));
                            if (n === t)
                                return this[u].slice();
                            var r, i = e.type(n), l = i === "array" || i === "object" ? n : arguments, p = this[u].slice();
                            return c(o, function(e, t) {
                                var n = l[i === "object" ? e : t.idx];
                                n == null && (n = p[t.idx]),
                                    p[t.idx] = h(n, t)
                            }),
                                f ? (r = s(f(p)),
                                    r[u] = p,
                                    r) : s(p)
                        }
                            ,
                            c(o, function(t, i) {
                                if (s.fn[t])
                                    return;
                                s.fn[t] = function(s) {
                                    var o = e.type(s), u = t === "alpha" ? this._hsla ? "hsla" : "rgba" : n, a = this[u](), f = a[i.idx], l;
                                    return o === "undefined" ? f : (o === "function" && (s = s.call(this, f),
                                        o = e.type(s)),
                                        s == null && i.empty ? this : (o === "string" && (l = r.exec(s),
                                        l && (s = f + parseFloat(l[2]) * (l[1] === "+" ? 1 : -1))),
                                            a[i.idx] = s,
                                            this[u](a)))
                                }
                            })
                    }),
                    s.hook = function(t) {
                        var n = t.split(" ");
                        c(n, function(t, n) {
                            e.cssHooks[n] = {
                                set: function(t, r) {
                                    var i, o, u = "";
                                    if (r !== "transparent" && (e.type(r) !== "string" || (i = p(r)))) {
                                        r = s(i || r);
                                        if (!a.rgba && r._rgba[3] !== 1) {
                                            o = n === "backgroundColor" ? t.parentNode : t;
                                            while ((u === "" || u === "transparent") && o && o.style)
                                                try {
                                                    u = e.css(o, "backgroundColor"),
                                                        o = o.parentNode
                                                } catch (f) {}
                                            r = r.blend(u && u !== "transparent" ? u : "_default")
                                        }
                                        r = r.toRgbaString()
                                    }
                                    try {
                                        t.style[n] = r
                                    } catch (f) {}
                                }
                            },
                                e.fx.step[n] = function(t) {
                                    t.colorInit || (t.start = s(t.elem, n),
                                        t.end = s(t.end),
                                        t.colorInit = !0),
                                        e.cssHooks[n].set(t.elem, t.start.transition(t.end, t.pos))
                                }
                        })
                    }
                    ,
                    s.hook(n),
                    e.cssHooks.borderColor = {
                        expand: function(e) {
                            var t = {};
                            return c(["Top", "Right", "Bottom", "Left"], function(n, r) {
                                t["border" + r + "Color"] = e
                            }),
                                t
                        }
                    },
                    l = e.Color.names = {
                        aqua: "#00ffff",
                        black: "#000000",
                        blue: "#0000ff",
                        fuchsia: "#ff00ff",
                        gray: "#808080",
                        green: "#008000",
                        lime: "#00ff00",
                        maroon: "#800000",
                        navy: "#000080",
                        olive: "#808000",
                        purple: "#800080",
                        red: "#ff0000",
                        silver: "#c0c0c0",
                        teal: "#008080",
                        white: "#ffffff",
                        yellow: "#ffff00",
                        transparent: [null, null, null, 0],
                        _default: "#ffffff"
                    }
            }(n),
            function() {
                function i(t) {
                    var n, r, i = t.ownerDocument.defaultView ? t.ownerDocument.defaultView.getComputedStyle(t, null) : t.currentStyle, s = {};
                    if (i && i.length && i[0] && i[i[0]]) {
                        r = i.length;
                        while (r--)
                            n = i[r],
                            typeof i[n] == "string" && (s[e.camelCase(n)] = i[n])
                    } else
                        for (n in i)
                            typeof i[n] == "string" && (s[n] = i[n]);
                    return s
                }
                function s(t, n) {
                    var i = {}, s, o;
                    for (s in n)
                        o = n[s],
                        t[s] !== o && !r[s] && (e.fx.step[s] || !isNaN(parseFloat(o))) && (i[s] = o);
                    return i
                }
                var t = ["add", "remove", "toggle"]
                    , r = {
                    border: 1,
                    borderBottom: 1,
                    borderColor: 1,
                    borderLeft: 1,
                    borderRight: 1,
                    borderTop: 1,
                    borderWidth: 1,
                    margin: 1,
                    padding: 1
                };
                e.each(["borderLeftStyle", "borderRightStyle", "borderBottomStyle", "borderTopStyle"], function(t, r) {
                    e.fx.step[r] = function(e) {
                        if (e.end !== "none" && !e.setAttr || e.pos === 1 && !e.setAttr)
                            n.style(e.elem, r, e.end),
                                e.setAttr = !0
                    }
                }),
                e.fn.addBack || (e.fn.addBack = function(e) {
                        return this.add(e == null ? this.prevObject : this.prevObject.filter(e))
                    }
                ),
                    e.effects.animateClass = function(n, r, o, u) {
                        var a = e.speed(r, o, u);
                        return this.queue(function() {
                            var r = e(this), o = r.attr("class") || "", u, f = a.children ? r.find("*").addBack() : r;
                            f = f.map(function() {
                                var t = e(this);
                                return {
                                    el: t,
                                    start: i(this)
                                }
                            }),
                                u = function() {
                                    e.each(t, function(e, t) {
                                        n[t] && r[t + "Class"](n[t])
                                    })
                                }
                                ,
                                u(),
                                f = f.map(function() {
                                    return this.end = i(this.el[0]),
                                        this.diff = s(this.start, this.end),
                                        this
                                }),
                                r.attr("class", o),
                                f = f.map(function() {
                                    var t = this
                                        , n = e.Deferred()
                                        , r = e.extend({}, a, {
                                        queue: !1,
                                        complete: function() {
                                            n.resolve(t)
                                        }
                                    });
                                    return this.el.animate(this.diff, r),
                                        n.promise()
                                }),
                                e.when.apply(e, f.get()).done(function() {
                                    u(),
                                        e.each(arguments, function() {
                                            var t = this.el;
                                            e.each(this.diff, function(e) {
                                                t.css(e, "")
                                            })
                                        }),
                                        a.complete.call(r[0])
                                })
                        })
                    }
                    ,
                    e.fn.extend({
                        addClass: function(t) {
                            return function(n, r, i, s) {
                                return r ? e.effects.animateClass.call(this, {
                                    add: n
                                }, r, i, s) : t.apply(this, arguments)
                            }
                        }(e.fn.addClass),
                        removeClass: function(t) {
                            return function(n, r, i, s) {
                                return arguments.length > 1 ? e.effects.animateClass.call(this, {
                                    remove: n
                                }, r, i, s) : t.apply(this, arguments)
                            }
                        }(e.fn.removeClass),
                        toggleClass: function(t) {
                            return function(n, r, i, s, o) {
                                return typeof r == "boolean" || r === undefined ? i ? e.effects.animateClass.call(this, r ? {
                                    add: n
                                } : {
                                    remove: n
                                }, i, s, o) : t.apply(this, arguments) : e.effects.animateClass.call(this, {
                                    toggle: n
                                }, r, i, s)
                            }
                        }(e.fn.toggleClass),
                        switchClass: function(t, n, r, i, s) {
                            return e.effects.animateClass.call(this, {
                                add: n,
                                remove: t
                            }, r, i, s)
                        }
                    })
            }(),
            function() {
                function n(t, n, r, i) {
                    e.isPlainObject(t) && (n = t,
                        t = t.effect),
                        t = {
                            effect: t
                        },
                    n == null && (n = {}),
                    e.isFunction(n) && (i = n,
                        r = null,
                        n = {});
                    if (typeof n == "number" || e.fx.speeds[n])
                        i = r,
                            r = n,
                            n = {};
                    return e.isFunction(r) && (i = r,
                        r = null),
                    n && e.extend(t, n),
                        r = r || n.duration,
                        t.duration = e.fx.off ? 0 : typeof r == "number" ? r : r in e.fx.speeds ? e.fx.speeds[r] : e.fx.speeds._default,
                        t.complete = i || n.complete,
                        t
                }
                function r(t) {
                    return !t || typeof t == "number" || e.fx.speeds[t] ? !0 : typeof t == "string" && !e.effects.effect[t] ? !0 : e.isFunction(t) ? !0 : typeof t == "object" && !t.effect ? !0 : !1
                }
                e.extend(e.effects, {
                    version: "1.11.4",
                    save: function(e, n) {
                        for (var r = 0; r < n.length; r++)
                            n[r] !== null && e.data(t + n[r], e[0].style[n[r]])
                    },
                    restore: function(e, n) {
                        var r, i;
                        for (i = 0; i < n.length; i++)
                            n[i] !== null && (r = e.data(t + n[i]),
                            r === undefined && (r = ""),
                                e.css(n[i], r))
                    },
                    setMode: function(e, t) {
                        return t === "toggle" && (t = e.is(":hidden") ? "show" : "hide"),
                            t
                    },
                    getBaseline: function(e, t) {
                        var n, r;
                        switch (e[0]) {
                            case "top":
                                n = 0;
                                break;
                            case "middle":
                                n = .5;
                                break;
                            case "bottom":
                                n = 1;
                                break;
                            default:
                                n = e[0] / t.height
                        }
                        switch (e[1]) {
                            case "left":
                                r = 0;
                                break;
                            case "center":
                                r = .5;
                                break;
                            case "right":
                                r = 1;
                                break;
                            default:
                                r = e[1] / t.width
                        }
                        return {
                            x: r,
                            y: n
                        }
                    },
                    createWrapper: function(t) {
                        if (t.parent().is(".ui-effects-wrapper"))
                            return t.parent();
                        var n = {
                            width: t.outerWidth(!0),
                            height: t.outerHeight(!0),
                            "float": t.css("float")
                        }
                            , r = e("<div></div>").addClass("ui-effects-wrapper").css({
                            fontSize: "100%",
                            background: "transparent",
                            border: "none",
                            margin: 0,
                            padding: 0
                        })
                            , i = {
                            width: t.width(),
                            height: t.height()
                        }
                            , s = document.activeElement;
                        try {
                            s.id
                        } catch (o) {
                            s = document.body
                        }
                        return t.wrap(r),
                        (t[0] === s || e.contains(t[0], s)) && e(s).focus(),
                            r = t.parent(),
                            t.css("position") === "static" ? (r.css({
                                position: "relative"
                            }),
                                t.css({
                                    position: "relative"
                                })) : (e.extend(n, {
                                position: t.css("position"),
                                zIndex: t.css("z-index")
                            }),
                                e.each(["top", "left", "bottom", "right"], function(e, r) {
                                    n[r] = t.css(r),
                                    isNaN(parseInt(n[r], 10)) && (n[r] = "auto")
                                }),
                                t.css({
                                    position: "relative",
                                    top: 0,
                                    left: 0,
                                    right: "auto",
                                    bottom: "auto"
                                })),
                            t.css(i),
                            r.css(n).show()
                    },
                    removeWrapper: function(t) {
                        var n = document.activeElement;
                        return t.parent().is(".ui-effects-wrapper") && (t.parent().replaceWith(t),
                        (t[0] === n || e.contains(t[0], n)) && e(n).focus()),
                            t
                    },
                    setTransition: function(t, n, r, i) {
                        return i = i || {},
                            e.each(n, function(e, n) {
                                var s = t.cssUnit(n);
                                s[0] > 0 && (i[n] = s[0] * r + s[1])
                            }),
                            i
                    }
                }),
                    e.fn.extend({
                        effect: function() {
                            function o(n) {
                                function u() {
                                    e.isFunction(i) && i.call(r[0]),
                                    e.isFunction(n) && n()
                                }
                                var r = e(this)
                                    , i = t.complete
                                    , o = t.mode;
                                (r.is(":hidden") ? o === "hide" : o === "show") ? (r[o](),
                                    u()) : s.call(r[0], t, u)
                            }
                            var t = n.apply(this, arguments)
                                , r = t.mode
                                , i = t.queue
                                , s = e.effects.effect[t.effect];
                            return e.fx.off || !s ? r ? this[r](t.duration, t.complete) : this.each(function() {
                                t.complete && t.complete.call(this)
                            }) : i === !1 ? this.each(o) : this.queue(i || "fx", o)
                        },
                        show: function(e) {
                            return function(t) {
                                if (r(t))
                                    return e.apply(this, arguments);
                                var i = n.apply(this, arguments);
                                return i.mode = "show",
                                    this.effect.call(this, i)
                            }
                        }(e.fn.show),
                        hide: function(e) {
                            return function(t) {
                                if (r(t))
                                    return e.apply(this, arguments);
                                var i = n.apply(this, arguments);
                                return i.mode = "hide",
                                    this.effect.call(this, i)
                            }
                        }(e.fn.hide),
                        toggle: function(e) {
                            return function(t) {
                                if (r(t) || typeof t == "boolean")
                                    return e.apply(this, arguments);
                                var i = n.apply(this, arguments);
                                return i.mode = "toggle",
                                    this.effect.call(this, i)
                            }
                        }(e.fn.toggle),
                        cssUnit: function(t) {
                            var n = this.css(t)
                                , r = [];
                            return e.each(["em", "px", "%", "pt"], function(e, t) {
                                n.indexOf(t) > 0 && (r = [parseFloat(n), t])
                            }),
                                r
                        }
                    })
            }(),
            function() {
                var t = {};
                e.each(["Quad", "Cubic", "Quart", "Quint", "Expo"], function(e, n) {
                    t[n] = function(t) {
                        return Math.pow(t, e + 2)
                    }
                }),
                    e.extend(t, {
                        Sine: function(e) {
                            return 1 - Math.cos(e * Math.PI / 2)
                        },
                        Circ: function(e) {
                            return 1 - Math.sqrt(1 - e * e)
                        },
                        Elastic: function(e) {
                            return e === 0 || e === 1 ? e : -Math.pow(2, 8 * (e - 1)) * Math.sin(((e - 1) * 80 - 7.5) * Math.PI / 15)
                        },
                        Back: function(e) {
                            return e * e * (3 * e - 2)
                        },
                        Bounce: function(e) {
                            var t, n = 4;
                            while (e < ((t = Math.pow(2, --n)) - 1) / 11)
                                ;
                            return 1 / Math.pow(4, 3 - n) - 7.5625 * Math.pow((t * 3 - 2) / 22 - e, 2)
                        }
                    }),
                    e.each(t, function(t, n) {
                        e.easing["easeIn" + t] = n,
                            e.easing["easeOut" + t] = function(e) {
                                return 1 - n(1 - e)
                            }
                            ,
                            e.easing["easeInOut" + t] = function(e) {
                                return e < .5 ? n(e * 2) / 2 : 1 - n(e * -2 + 2) / 2
                            }
                    })
            }();
        var r = e.effects
            , i = e.effects.effect.blind = function(t, n) {
            var r = e(this), i = /up|down|vertical/, s = /up|left|vertical|horizontal/, o = ["position", "top", "bottom", "left", "right", "height", "width"], u = e.effects.setMode(r, t.mode || "hide"), a = t.direction || "up", f = i.test(a), l = f ? "height" : "width", c = f ? "top" : "left", h = s.test(a), p = {}, d = u === "show", v, m, g;
            r.parent().is(".ui-effects-wrapper") ? e.effects.save(r.parent(), o) : e.effects.save(r, o),
                r.show(),
                v = e.effects.createWrapper(r).css({
                    overflow: "hidden"
                }),
                m = v[l](),
                g = parseFloat(v.css(c)) || 0,
                p[l] = d ? m : 0,
            h || (r.css(f ? "bottom" : "right", 0).css(f ? "top" : "left", "auto").css({
                position: "absolute"
            }),
                p[c] = d ? g : m + g),
            d && (v.css(l, 0),
            h || v.css(c, g + m)),
                v.animate(p, {
                    duration: t.duration,
                    easing: t.easing,
                    queue: !1,
                    complete: function() {
                        u === "hide" && r.hide(),
                            e.effects.restore(r, o),
                            e.effects.removeWrapper(r),
                            n()
                    }
                })
        }
            , s = e.effects.effect.bounce = function(t, n) {
            var r = e(this), i = ["position", "top", "bottom", "left", "right", "height", "width"], s = e.effects.setMode(r, t.mode || "effect"), o = s === "hide", u = s === "show", a = t.direction || "up", f = t.distance, l = t.times || 5, c = l * 2 + (u || o ? 1 : 0), h = t.duration / c, p = t.easing, d = a === "up" || a === "down" ? "top" : "left", v = a === "up" || a === "left", m, g, y, b = r.queue(), w = b.length;
            (u || o) && i.push("opacity"),
                e.effects.save(r, i),
                r.show(),
                e.effects.createWrapper(r),
            f || (f = r[d === "top" ? "outerHeight" : "outerWidth"]() / 3),
            u && (y = {
                opacity: 1
            },
                y[d] = 0,
                r.css("opacity", 0).css(d, v ? -f * 2 : f * 2).animate(y, h, p)),
            o && (f /= Math.pow(2, l - 1)),
                y = {},
                y[d] = 0;
            for (m = 0; m < l; m++)
                g = {},
                    g[d] = (v ? "-=" : "+=") + f,
                    r.animate(g, h, p).animate(y, h, p),
                    f = o ? f * 2 : f / 2;
            o && (g = {
                opacity: 0
            },
                g[d] = (v ? "-=" : "+=") + f,
                r.animate(g, h, p)),
                r.queue(function() {
                    o && r.hide(),
                        e.effects.restore(r, i),
                        e.effects.removeWrapper(r),
                        n()
                }),
            w > 1 && b.splice.apply(b, [1, 0].concat(b.splice(w, c + 1))),
                r.dequeue()
        }
            , o = e.effects.effect.clip = function(t, n) {
            var r = e(this), i = ["position", "top", "bottom", "left", "right", "height", "width"], s = e.effects.setMode(r, t.mode || "hide"), o = s === "show", u = t.direction || "vertical", a = u === "vertical", f = a ? "height" : "width", l = a ? "top" : "left", c = {}, h, p, d;
            e.effects.save(r, i),
                r.show(),
                h = e.effects.createWrapper(r).css({
                    overflow: "hidden"
                }),
                p = r[0].tagName === "IMG" ? h : r,
                d = p[f](),
            o && (p.css(f, 0),
                p.css(l, d / 2)),
                c[f] = o ? d : 0,
                c[l] = o ? 0 : d / 2,
                p.animate(c, {
                    queue: !1,
                    duration: t.duration,
                    easing: t.easing,
                    complete: function() {
                        o || r.hide(),
                            e.effects.restore(r, i),
                            e.effects.removeWrapper(r),
                            n()
                    }
                })
        }
            , u = e.effects.effect.drop = function(t, n) {
            var r = e(this), i = ["position", "top", "bottom", "left", "right", "opacity", "height", "width"], s = e.effects.setMode(r, t.mode || "hide"), o = s === "show", u = t.direction || "left", a = u === "up" || u === "down" ? "top" : "left", f = u === "up" || u === "left" ? "pos" : "neg", l = {
                opacity: o ? 1 : 0
            }, c;
            e.effects.save(r, i),
                r.show(),
                e.effects.createWrapper(r),
                c = t.distance || r[a === "top" ? "outerHeight" : "outerWidth"](!0) / 2,
            o && r.css("opacity", 0).css(a, f === "pos" ? -c : c),
                l[a] = (o ? f === "pos" ? "+=" : "-=" : f === "pos" ? "-=" : "+=") + c,
                r.animate(l, {
                    queue: !1,
                    duration: t.duration,
                    easing: t.easing,
                    complete: function() {
                        s === "hide" && r.hide(),
                            e.effects.restore(r, i),
                            e.effects.removeWrapper(r),
                            n()
                    }
                })
        }
            , a = e.effects.effect.explode = function(t, n) {
            function y() {
                c.push(this),
                c.length === r * i && b()
            }
            function b() {
                s.css({
                    visibility: "visible"
                }),
                    e(c).remove(),
                u || s.hide(),
                    n()
            }
            var r = t.pieces ? Math.round(Math.sqrt(t.pieces)) : 3, i = r, s = e(this), o = e.effects.setMode(s, t.mode || "hide"), u = o === "show", a = s.show().css("visibility", "hidden").offset(), f = Math.ceil(s.outerWidth() / i), l = Math.ceil(s.outerHeight() / r), c = [], h, p, d, v, m, g;
            for (h = 0; h < r; h++) {
                v = a.top + h * l,
                    g = h - (r - 1) / 2;
                for (p = 0; p < i; p++)
                    d = a.left + p * f,
                        m = p - (i - 1) / 2,
                        s.clone().appendTo("body").wrap("<div></div>").css({
                            position: "absolute",
                            visibility: "visible",
                            left: -p * f,
                            top: -h * l
                        }).parent().addClass("ui-effects-explode").css({
                            position: "absolute",
                            overflow: "hidden",
                            width: f,
                            height: l,
                            left: d + (u ? m * f : 0),
                            top: v + (u ? g * l : 0),
                            opacity: u ? 0 : 1
                        }).animate({
                            left: d + (u ? 0 : m * f),
                            top: v + (u ? 0 : g * l),
                            opacity: u ? 1 : 0
                        }, t.duration || 500, t.easing, y)
            }
        }
            , f = e.effects.effect.fade = function(t, n) {
            var r = e(this)
                , i = e.effects.setMode(r, t.mode || "toggle");
            r.animate({
                opacity: i
            }, {
                queue: !1,
                duration: t.duration,
                easing: t.easing,
                complete: n
            })
        }
            , l = e.effects.effect.fold = function(t, n) {
            var r = e(this), i = ["position", "top", "bottom", "left", "right", "height", "width"], s = e.effects.setMode(r, t.mode || "hide"), o = s === "show", u = s === "hide", a = t.size || 15, f = /([0-9]+)%/.exec(a), l = !!t.horizFirst, c = o !== l, h = c ? ["width", "height"] : ["height", "width"], p = t.duration / 2, d, v, m = {}, g = {};
            e.effects.save(r, i),
                r.show(),
                d = e.effects.createWrapper(r).css({
                    overflow: "hidden"
                }),
                v = c ? [d.width(), d.height()] : [d.height(), d.width()],
            f && (a = parseInt(f[1], 10) / 100 * v[u ? 0 : 1]),
            o && d.css(l ? {
                height: 0,
                width: a
            } : {
                height: a,
                width: 0
            }),
                m[h[0]] = o ? v[0] : a,
                g[h[1]] = o ? v[1] : 0,
                d.animate(m, p, t.easing).animate(g, p, t.easing, function() {
                    u && r.hide(),
                        e.effects.restore(r, i),
                        e.effects.removeWrapper(r),
                        n()
                })
        }
            , c = e.effects.effect.highlight = function(t, n) {
            var r = e(this)
                , i = ["backgroundImage", "backgroundColor", "opacity"]
                , s = e.effects.setMode(r, t.mode || "show")
                , o = {
                backgroundColor: r.css("backgroundColor")
            };
            s === "hide" && (o.opacity = 0),
                e.effects.save(r, i),
                r.show().css({
                    backgroundImage: "none",
                    backgroundColor: t.color || "#ffff99"
                }).animate(o, {
                    queue: !1,
                    duration: t.duration,
                    easing: t.easing,
                    complete: function() {
                        s === "hide" && r.hide(),
                            e.effects.restore(r, i),
                            n()
                    }
                })
        }
            , h = e.effects.effect.size = function(t, n) {
            var r, i, s, o = e(this), u = ["position", "top", "bottom", "left", "right", "width", "height", "overflow", "opacity"], a = ["position", "top", "bottom", "left", "right", "overflow", "opacity"], f = ["width", "height", "overflow"], l = ["fontSize"], c = ["borderTopWidth", "borderBottomWidth", "paddingTop", "paddingBottom"], h = ["borderLeftWidth", "borderRightWidth", "paddingLeft", "paddingRight"], p = e.effects.setMode(o, t.mode || "effect"), d = t.restore || p !== "effect", v = t.scale || "both", m = t.origin || ["middle", "center"], g = o.css("position"), y = d ? u : a, b = {
                height: 0,
                width: 0,
                outerHeight: 0,
                outerWidth: 0
            };
            p === "show" && o.show(),
                r = {
                    height: o.height(),
                    width: o.width(),
                    outerHeight: o.outerHeight(),
                    outerWidth: o.outerWidth()
                },
                t.mode === "toggle" && p === "show" ? (o.from = t.to || b,
                    o.to = t.from || r) : (o.from = t.from || (p === "show" ? b : r),
                    o.to = t.to || (p === "hide" ? b : r)),
                s = {
                    from: {
                        y: o.from.height / r.height,
                        x: o.from.width / r.width
                    },
                    to: {
                        y: o.to.height / r.height,
                        x: o.to.width / r.width
                    }
                };
            if (v === "box" || v === "both")
                s.from.y !== s.to.y && (y = y.concat(c),
                    o.from = e.effects.setTransition(o, c, s.from.y, o.from),
                    o.to = e.effects.setTransition(o, c, s.to.y, o.to)),
                s.from.x !== s.to.x && (y = y.concat(h),
                    o.from = e.effects.setTransition(o, h, s.from.x, o.from),
                    o.to = e.effects.setTransition(o, h, s.to.x, o.to));
            (v === "content" || v === "both") && s.from.y !== s.to.y && (y = y.concat(l).concat(f),
                o.from = e.effects.setTransition(o, l, s.from.y, o.from),
                o.to = e.effects.setTransition(o, l, s.to.y, o.to)),
                e.effects.save(o, y),
                o.show(),
                e.effects.createWrapper(o),
                o.css("overflow", "hidden").css(o.from),
            m && (i = e.effects.getBaseline(m, r),
                o.from.top = (r.outerHeight - o.outerHeight()) * i.y,
                o.from.left = (r.outerWidth - o.outerWidth()) * i.x,
                o.to.top = (r.outerHeight - o.to.outerHeight) * i.y,
                o.to.left = (r.outerWidth - o.to.outerWidth) * i.x),
                o.css(o.from);
            if (v === "content" || v === "both")
                c = c.concat(["marginTop", "marginBottom"]).concat(l),
                    h = h.concat(["marginLeft", "marginRight"]),
                    f = u.concat(c).concat(h),
                    o.find("*[width]").each(function() {
                        var n = e(this)
                            , r = {
                            height: n.height(),
                            width: n.width(),
                            outerHeight: n.outerHeight(),
                            outerWidth: n.outerWidth()
                        };
                        d && e.effects.save(n, f),
                            n.from = {
                                height: r.height * s.from.y,
                                width: r.width * s.from.x,
                                outerHeight: r.outerHeight * s.from.y,
                                outerWidth: r.outerWidth * s.from.x
                            },
                            n.to = {
                                height: r.height * s.to.y,
                                width: r.width * s.to.x,
                                outerHeight: r.height * s.to.y,
                                outerWidth: r.width * s.to.x
                            },
                        s.from.y !== s.to.y && (n.from = e.effects.setTransition(n, c, s.from.y, n.from),
                            n.to = e.effects.setTransition(n, c, s.to.y, n.to)),
                        s.from.x !== s.to.x && (n.from = e.effects.setTransition(n, h, s.from.x, n.from),
                            n.to = e.effects.setTransition(n, h, s.to.x, n.to)),
                            n.css(n.from),
                            n.animate(n.to, t.duration, t.easing, function() {
                                d && e.effects.restore(n, f)
                            })
                    });
            o.animate(o.to, {
                queue: !1,
                duration: t.duration,
                easing: t.easing,
                complete: function() {
                    o.to.opacity === 0 && o.css("opacity", o.from.opacity),
                    p === "hide" && o.hide(),
                        e.effects.restore(o, y),
                    d || (g === "static" ? o.css({
                        position: "relative",
                        top: o.to.top,
                        left: o.to.left
                    }) : e.each(["top", "left"], function(e, t) {
                        o.css(t, function(t, n) {
                            var r = parseInt(n, 10)
                                , i = e ? o.to.left : o.to.top;
                            return n === "auto" ? i + "px" : r + i + "px"
                        })
                    })),
                        e.effects.removeWrapper(o),
                        n()
                }
            })
        }
            , p = e.effects.effect.scale = function(t, n) {
            var r = e(this)
                , i = e.extend(!0, {}, t)
                , s = e.effects.setMode(r, t.mode || "effect")
                , o = parseInt(t.percent, 10) || (parseInt(t.percent, 10) === 0 ? 0 : s === "hide" ? 0 : 100)
                , u = t.direction || "both"
                , a = t.origin
                , f = {
                height: r.height(),
                width: r.width(),
                outerHeight: r.outerHeight(),
                outerWidth: r.outerWidth()
            }
                , l = {
                y: u !== "horizontal" ? o / 100 : 1,
                x: u !== "vertical" ? o / 100 : 1
            };
            i.effect = "size",
                i.queue = !1,
                i.complete = n,
            s !== "effect" && (i.origin = a || ["middle", "center"],
                i.restore = !0),
                i.from = t.from || (s === "show" ? {
                        height: 0,
                        width: 0,
                        outerHeight: 0,
                        outerWidth: 0
                    } : f),
                i.to = {
                    height: f.height * l.y,
                    width: f.width * l.x,
                    outerHeight: f.outerHeight * l.y,
                    outerWidth: f.outerWidth * l.x
                },
            i.fade && (s === "show" && (i.from.opacity = 0,
                i.to.opacity = 1),
            s === "hide" && (i.from.opacity = 1,
                i.to.opacity = 0)),
                r.effect(i)
        }
            , d = e.effects.effect.puff = function(t, n) {
            var r = e(this)
                , i = e.effects.setMode(r, t.mode || "hide")
                , s = i === "hide"
                , o = parseInt(t.percent, 10) || 150
                , u = o / 100
                , a = {
                height: r.height(),
                width: r.width(),
                outerHeight: r.outerHeight(),
                outerWidth: r.outerWidth()
            };
            e.extend(t, {
                effect: "scale",
                queue: !1,
                fade: !0,
                mode: i,
                complete: n,
                percent: s ? o : 100,
                from: s ? a : {
                    height: a.height * u,
                    width: a.width * u,
                    outerHeight: a.outerHeight * u,
                    outerWidth: a.outerWidth * u
                }
            }),
                r.effect(t)
        }
            , v = e.effects.effect.pulsate = function(t, n) {
            var r = e(this), i = e.effects.setMode(r, t.mode || "show"), s = i === "show", o = i === "hide", u = s || i === "hide", a = (t.times || 5) * 2 + (u ? 1 : 0), f = t.duration / a, l = 0, c = r.queue(), h = c.length, p;
            if (s || !r.is(":visible"))
                r.css("opacity", 0).show(),
                    l = 1;
            for (p = 1; p < a; p++)
                r.animate({
                    opacity: l
                }, f, t.easing),
                    l = 1 - l;
            r.animate({
                opacity: l
            }, f, t.easing),
                r.queue(function() {
                    o && r.hide(),
                        n()
                }),
            h > 1 && c.splice.apply(c, [1, 0].concat(c.splice(h, a + 1))),
                r.dequeue()
        }
            , m = e.effects.effect.shake = function(t, n) {
            var r = e(this), i = ["position", "top", "bottom", "left", "right", "height", "width"], s = e.effects.setMode(r, t.mode || "effect"), o = t.direction || "left", u = t.distance || 20, a = t.times || 3, f = a * 2 + 1, l = Math.round(t.duration / f), c = o === "up" || o === "down" ? "top" : "left", h = o === "up" || o === "left", p = {}, d = {}, v = {}, m, g = r.queue(), y = g.length;
            e.effects.save(r, i),
                r.show(),
                e.effects.createWrapper(r),
                p[c] = (h ? "-=" : "+=") + u,
                d[c] = (h ? "+=" : "-=") + u * 2,
                v[c] = (h ? "-=" : "+=") + u * 2,
                r.animate(p, l, t.easing);
            for (m = 1; m < a; m++)
                r.animate(d, l, t.easing).animate(v, l, t.easing);
            r.animate(d, l, t.easing).animate(p, l / 2, t.easing).queue(function() {
                s === "hide" && r.hide(),
                    e.effects.restore(r, i),
                    e.effects.removeWrapper(r),
                    n()
            }),
            y > 1 && g.splice.apply(g, [1, 0].concat(g.splice(y, f + 1))),
                r.dequeue()
        }
            , g = e.effects.effect.slide = function(t, n) {
            var r = e(this), i = ["position", "top", "bottom", "left", "right", "width", "height"], s = e.effects.setMode(r, t.mode || "show"), o = s === "show", u = t.direction || "left", a = u === "up" || u === "down" ? "top" : "left", f = u === "up" || u === "left", l, c = {};
            e.effects.save(r, i),
                r.show(),
                l = t.distance || r[a === "top" ? "outerHeight" : "outerWidth"](!0),
                e.effects.createWrapper(r).css({
                    overflow: "hidden"
                }),
            o && r.css(a, f ? isNaN(l) ? "-" + l : -l : l),
                c[a] = (o ? f ? "+=" : "-=" : f ? "-=" : "+=") + l,
                r.animate(c, {
                    queue: !1,
                    duration: t.duration,
                    easing: t.easing,
                    complete: function() {
                        s === "hide" && r.hide(),
                            e.effects.restore(r, i),
                            e.effects.removeWrapper(r),
                            n()
                    }
                })
        }
            , y = e.effects.effect.transfer = function(t, n) {
            var r = e(this)
                , i = e(t.to)
                , s = i.css("position") === "fixed"
                , o = e("body")
                , u = s ? o.scrollTop() : 0
                , a = s ? o.scrollLeft() : 0
                , f = i.offset()
                , l = {
                top: f.top - u,
                left: f.left - a,
                height: i.innerHeight(),
                width: i.innerWidth()
            }
                , c = r.offset()
                , h = e("<div class='ui-effects-transfer'></div>").appendTo(document.body).addClass(t.className).css({
                top: c.top - u,
                left: c.left - a,
                height: r.innerHeight(),
                width: r.innerWidth(),
                position: s ? "fixed" : "absolute"
            }).animate(l, t.duration, t.easing, function() {
                h.remove(),
                    n()
            })
        }
    }),
    function(e) {
        e.fn.extend({
            slimScroll: function(n) {
                var r = {
                    width: "auto",
                    height: "250px",
                    size: "7px",
                    color: "#000",
                    position: "right",
                    distance: "1px",
                    start: "top",
                    opacity: .4,
                    alwaysVisible: !1,
                    disableFadeOut: !1,
                    railVisible: !1,
                    railColor: "#333",
                    railOpacity: .2,
                    railDraggable: !0,
                    railClass: "slimScrollRail",
                    barClass: "slimScrollBar",
                    wrapperClass: "slimScrollDiv",
                    allowPageScroll: !1,
                    wheelStep: 20,
                    touchScrollStep: 200,
                    borderRadius: "7px",
                    railBorderRadius: "7px"
                }
                    , i = e.extend(r, n);
                return this.each(function() {
                    function T(t) {
                        if (!r)
                            return;
                        var t = t || window.event
                            , n = 0;
                        t.wheelDelta && (n = -t.wheelDelta / 120),
                        t.detail && (n = t.detail / 3);
                        var s = t.target || t.srcTarget || t.srcElement;
                        e(s).closest("." + i.wrapperClass).is(m.parent()) && N(n, !0),
                        t.preventDefault && !v && t.preventDefault(),
                        v || (t.returnValue = !1)
                    }
                    function N(e, t, n) {
                        v = !1;
                        var r = e
                            , s = m.outerHeight() - S.outerHeight();
                        t && (r = parseInt(S.css("top")) + e * parseInt(i.wheelStep) / 100 * S.outerHeight(),
                            r = Math.min(Math.max(r, 0), s),
                            r = e > 0 ? Math.ceil(r) : Math.floor(r),
                            S.css({
                                top: r + "px"
                            })),
                            c = parseInt(S.css("top")) / (m.outerHeight() - S.outerHeight()),
                            r = c * (m[0].scrollHeight - m.outerHeight());
                        if (n) {
                            r = e;
                            var u = r / m[0].scrollHeight * m.outerHeight();
                            u = Math.min(Math.max(u, 0), s),
                                S.css({
                                    top: u + "px"
                                })
                        }
                        m.scrollTop(r),
                            m.trigger("slimscrolling", ~~r),
                            L(),
                            A()
                    }
                    function C(e) {
                        window.addEventListener ? (e.addEventListener("DOMMouseScroll", T, !1),
                            e.addEventListener("mousewheel", T, !1)) : document.attachEvent("onmousewheel", T)
                    }
                    function k() {
                        l = Math.max(m.outerHeight() / m[0].scrollHeight * m.outerHeight(), d),
                            S.css({
                                height: l + "px"
                            });
                        var e = l == m.outerHeight() ? "none" : "block";
                        S.css({
                            display: e
                        })
                    }
                    function L() {
                        k(),
                            clearTimeout(a);
                        if (c == ~~c) {
                            v = i.allowPageScroll;
                            if (h != c) {
                                var e = ~~c == 0 ? "top" : "bottom";
                                m.trigger("slimscroll", e)
                            }
                        } else
                            v = !1;
                        h = c;
                        if (l >= m.outerHeight()) {
                            v = !0;
                            return
                        }
                        S.stop(!0, !0).fadeIn("fast"),
                        i.railVisible && E.stop(!0, !0).fadeIn("fast")
                    }
                    function A() {
                        i.alwaysVisible || (a = setTimeout(function() {
                            (!i.disableFadeOut || !r) && !s && !u && (S.fadeOut("slow"),
                                E.fadeOut("slow"))
                        }, 1e3))
                    }
                    var r, s, u, a, f, l, c, h, p = "<div></div>", d = 30, v = !1, m = e(this);
                    if (m.parent().hasClass(i.wrapperClass)) {
                        var g = m.scrollTop();
                        S = m.siblings("." + i.barClass),
                            E = m.siblings("." + i.railClass),
                            k();
                        if (e.isPlainObject(n)) {
                            if ("height"in n && n.height == "auto") {
                                m.parent().css("height", "auto"),
                                    m.css("height", "auto");
                                var y = m.parent().parent().height();
                                m.parent().css("height", y),
                                    m.css("height", y)
                            } else if ("height"in n) {
                                var b = n.height;
                                m.parent().css("height", b),
                                    m.css("height", b)
                            }
                            if ("scrollTo"in n)
                                g = parseInt(i.scrollTo);
                            else if ("scrollBy"in n)
                                g += parseInt(i.scrollBy);
                            else if ("destroy"in n) {
                                S.remove(),
                                    E.remove(),
                                    m.unwrap();
                                return
                            }
                            N(g, !1, !0)
                        }
                        return
                    }
                    if (e.isPlainObject(n) && "destroy"in n)
                        return;
                    i.height = i.height == "auto" ? m.parent().height() : i.height;
                    var w = e(p).addClass(i.wrapperClass).css({
                        position: "relative",
                        overflow: "hidden",
                        width: i.width,
                        height: i.height
                    });
                    m.css({
                        overflow: "hidden",
                        width: i.width,
                        height: i.height
                    });
                    var E = e(p).addClass(i.railClass).css({
                        width: i.size,
                        height: "100%",
                        position: "absolute",
                        top: 0,
                        display: i.alwaysVisible && i.railVisible ? "block" : "none",
                        "border-radius": i.railBorderRadius,
                        background: i.railColor,
                        opacity: i.railOpacity,
                        zIndex: 90
                    })
                        , S = e(p).addClass(i.barClass).css({
                        background: i.color,
                        width: i.size,
                        position: "absolute",
                        top: 0,
                        opacity: i.opacity,
                        display: i.alwaysVisible ? "block" : "none",
                        "border-radius": i.borderRadius,
                        BorderRadius: i.borderRadius,
                        MozBorderRadius: i.borderRadius,
                        WebkitBorderRadius: i.borderRadius,
                        zIndex: 99
                    })
                        , x = i.position == "right" ? {
                        right: i.distance
                    } : {
                        left: i.distance
                    };
                    E.css(x),
                        S.css(x),
                        m.wrap(w),
                        m.parent().append(S),
                        m.parent().append(E),
                    i.railDraggable && S.bind("mousedown", function(n) {
                        var r = e(document);
                        return u = !0,
                            t = parseFloat(S.css("top")),
                            pageY = n.pageY,
                            r.bind("mousemove.slimscroll", function(e) {
                                currTop = t + e.pageY - pageY,
                                    S.css("top", currTop),
                                    N(0, S.position().top, !1)
                            }),
                            r.bind("mouseup.slimscroll", function(e) {
                                u = !1,
                                    A(),
                                    r.unbind(".slimscroll")
                            }),
                            !1
                    }).bind("selectstart.slimscroll", function(e) {
                        return e.stopPropagation(),
                            e.preventDefault(),
                            !1
                    }),
                        E.hover(function() {
                            L()
                        }, function() {
                            A()
                        }),
                        S.hover(function() {
                            s = !0
                        }, function() {
                            s = !1
                        }),
                        m.hover(function() {
                            r = !0,
                                L(),
                                A()
                        }, function() {
                            r = !1,
                                A()
                        }),
                        m.bind("touchstart", function(e, t) {
                            e.originalEvent.touches.length && (f = e.originalEvent.touches[0].pageY)
                        }),
                        m.bind("touchmove", function(e) {
                            v || e.originalEvent.preventDefault();
                            if (e.originalEvent.touches.length) {
                                var t = (f - e.originalEvent.touches[0].pageY) / i.touchScrollStep;
                                N(t, !0),
                                    f = e.originalEvent.touches[0].pageY
                            }
                        }),
                        k(),
                        i.start === "bottom" ? (S.css({
                            top: m.outerHeight() - S.outerHeight()
                        }),
                            N(0, !0)) : i.start !== "top" && (N(e(i.start).position().top, null, !0),
                        i.alwaysVisible || S.hide()),
                        C(this)
                }),
                    this
            }
        }),
            e.fn.extend({
                slimscroll: e.fn.slimScroll
            })
    }(jQuery),
    define("slimScroll", function() {}),
    define("complaint", ["avalon", "uiService", "validators", "jquery", "slimScroll", "bootstrap"], function(e, t, n, r) {
        r("#complaintModal").modal({
            show: !1,
            backdrop: "static"
        });
        var i = r("#companyNameHidden").val()
            , s = e.define({
            $id: "complaintCtrl",
            seconds: 10,
            step: 2,
            closeDuration: 10,
            closeTimerHandler: -1,
            next: function() {
                s.step = 2
            },
            submit: function() {
                o.isValid() && (t.blockUI.block(),
                    r.ajax({
                        type: "POST",
                        url: "/misc/submit-complaint",
                        dataType: "json",
                        data: {
                            company: s.data.company.value,
                            content: s.data.content.value,
                            name: s.data.name.value,
                            phone: s.data.phone.value,
                            email: s.data.email.value
                        }
                    }).done(function(e) {
                        e.errcode ? t.notification.error(e.message, "错误！") : (t.notification.success("我们已经收到您的申诉，谢谢您的支持！", "非常感谢！"),
                            s.step = 3)
                    }).fail(function() {
                        t.notification.error("系统错误", "错误")
                    }).always(function() {
                        t.blockUI.unblock()
                    }))
            },
            reset: function() {
                s.step = 1,
                    s.data.company.value = i,
                    s.data.content.value = "",
                    s.data.name.value = "",
                    s.data.phone.value = "",
                    s.data.email.value = ""
            },
            data: {
                company: {
                    value: i,
                    validator: ["required", "forbiden"],
                    error: ""
                },
                content: {
                    value: "",
                    validator: ["required", "forbiden"],
                    error: ""
                },
                name: {
                    value: "",
                    validator: ["required", "forbiden"],
                    error: ""
                },
                phone: {
                    value: "",
                    validator: ["required", "phone"],
                    error: ""
                },
                email: {
                    value: "",
                    validator: ["email"],
                    error: ""
                }
            }
        })
            , o = n.init([s.data.company, s.data.content, s.data.name, s.data.phone, s.data.email]);
        s.$watch("step", function(e) {
            if (e == 3) {
                var t = function() {
                    s.closeDuration--,
                        s.closeDuration > 0 ? s.closeTimerHandler = setTimeout(t, 1e3) : r("#complaintModal").modal("hide")
                };
                t()
            }
        }),
            r(function() {
                r("#btn-complaint").click(function() {
                    r("#complaintModal").modal("show")
                }),
                    r("#complaintModal").on("shown.bs.modal", function(e) {
                        s.seconds = 10,
                            s.step = 1,
                            s.closeDuration = 10;
                        var t = function() {
                            setTimeout(function() {
                                s.seconds--,
                                s.seconds > 0 && t()
                            }, 1e3)
                        };
                        t()
                    }),
                    r("#complaintModal").on("hidden.bs.modal", function(e) {
                        s.reset(),
                        s.closeTimerHandler != -1 && (clearTimeout(s.closeTimerHandler),
                            s.closeTimerHandler = -1)
                    }),
                    r("#complaint-instruction").slimscroll({
                        height: "385px",
                        size: "4px",
                        color: "#5e89d7",
                        railVisible: !0,
                        railColor: "#e4e4e4",
                        opacity: 1,
                        railOpacity: .4,
                        alwaysVisible: !0
                    })
            })
    }),
    define("global", ["bloodhound", "bdStatisticsEvents", "notification", "limitwords", "common", "_", "js-cookie", "statistics", "typeahead", "placeholder", "feedback", "bootstrap", "jquery-effect", "complaint"], function(e, t, n, r, i, s, o, u) {
        function l(e) {
            var n = {};
            return $.each(t, function(t, r) {
                if (r.eventId == e)
                    return n = r,
                        !1
            }),
                n
        }
        function c() {
            var e = $(window).height()
                , t = 60
                , n = 50
                , r = $(".search-result").height()
                , i = $(".company").height()
                , s = 97;
            r + t + s + n < e && $(".search-result").css("min-height", e - t - s - n - 24),
            i + t + s < e && $(".with-min-height").css("min-height", e - t - s)
        }
        function h() {
            return window.getSelection ? window.getSelection().toString() : document.selection && document.selection.createRange ? document.selection.createRange().text : ""
        }
        function p(e) {
            var t, n, r = location.search.substr(1).split("&"), i;
            for (t = 0,
                     n = r.length; t < n; t++) {
                i = r[t].split("=");
                if (i[0] == e)
                    return i[1]
            }
        }
        var a = new e({
            datumTokenizer: e.tokenizers.obj.whitespace("name"),
            queryTokenizer: e.tokenizers.whitespace,
            remote: {
                url: "/service/suggestion?key=%QUERY",
                wildcard: "%QUERY"
            }
        })
            , f = s.template('<div style="overflow:hidden"><a href="/company/<%=eid%>" target="_blank"><span style="color:#999;width:20%"><%=category%></span><span style="color:#000;width:80%"><%=name||oper_name||credit_no||org_no||reg_no%></span></a></div>');
        return $("#searchBar").typeahead({
            hint: !0,
            highlight: !0,
            minLength: 2
        }, {
            name: "companies",
            source: a,
            display: "name",
            templates: {
                suggestion: f
            }
        }),
            $(".search-bar #searchBar").focus(),
        typeof user_status != "undefined" && (user_status == 1 && n.error("您的账户已过期，请重新登录!"),
        user_status == 2 && n.error("您的访问次数达到上限， 请登录或注册后再试！")),
            $(".user-avatar.nav_link").on("mouseover", function() {
                $(this).addClass("open")
            }),
            $(".user-avatar .dropdownPosition").on("mouseout", function() {
                $(".user-avatar.nav_link").removeClass("open")
            }),
            $(".tt-input").placeholder(),
            $(".aside-content").click(function(e) {
                e.currentTarget.id == "divGoTop" ? $("html, body").animate({
                    scrollTop: 0
                }, 400, "easeOutQuart") : e.currentTarget.id == "divFeedback" && $("#feedbackModal").modal("show")
            }),
            $(".aside-content").mouseover(function(e) {
                $(e.currentTarget).find("div").css("background-position", "0px -30px"),
                    $(e.currentTarget).find("p").css("color", "#000"),
                    $(e.currentTarget).css("background-color", "#FFC000")
            }),
            $(".aside-content").mouseout(function(e) {
                $(e.currentTarget).find("div").css("background-position", "0px 0px"),
                    $(e.currentTarget).find("p").css("color", "#618DBE"),
                    $(e.currentTarget).css("background-color", "#F8F8F8")
            }),
            $("[data-toggle='popover']").popover(),
            $(document).delegate("[eventId]", "click", function() {
                var e = this.attributes.eventId.value
                    , t = l(e);
                if (typeof _hmt == "undefined")
                    return;
                _hmt.push(["_trackEvent", t.category, "click", t.eventDesc])
            }),
            $(function() {
                if (!+o.get("hide-download-panel")) {
                    $("#download-container").show();
                    var e = 0
                        , t = ["深度挖掘企业与人的关联关系！", "新用户APP注册填写邀请码，即赠30天VIP！", "启信宝，让商业更真实!"]
                        , n = $("#download-container>.image-container>h3")
                        , r = !0
                        , i = function() {
                        n.effect("puff", {
                            mode: r ? "hide" : "show",
                            percent: 200
                        }, 600),
                            setTimeout(function() {
                                r = !r;
                                if (!r) {
                                    e++;
                                    var s = e % t.length;
                                    s == 2 ? n.css({
                                        color: "#44b6ae"
                                    }) : n.css({
                                        color: "#fff"
                                    }),
                                        n.text(t[s])
                                }
                                i()
                            }, r ? 800 : 4e3)
                    };
                    setTimeout(function() {
                        i()
                    }, 4e3)
                } else
                    $("#download-container-left").show()
            }),
            $(document).delegate("#close-download-panel", "click", function() {
                $("#download-container").fadeOut(),
                    $("#download-container-left").fadeIn(),
                    o.set("hide-download-panel", 1)
            }),
            $(document).delegate("#download-container-left", "click", function() {
                $("#download-container").fadeIn(),
                    $("#download-container-left").fadeOut(),
                    o.set("hide-download-panel", 0)
            }),
            $(document).delegate("#formTopSearch,#formIndex", "submit", function(e) {
                var t = $("#searchBar").val()
                    , i = r.parse(t);
                if (!i) {
                    n.info("请输入更精确关键字查询"),
                        e.preventDefault();
                    return
                }
                var s = {};
                if (this.id == "formTopSearch") {
                    var o = window.location.href;
                    o.indexOf("/search/hotwords") > -1 ? s = l("Q0139") : o.indexOf("/search/domain") > -1 ? s = l("Q0140") : o.indexOf("/search/prov") > -1 ? s = l("Q0141") : o.indexOf("/search/?key=") > -1 && o.indexOf("type=enterprise") > -1 ? s = l("Q0142") : o.indexOf("/search/?key=") > -1 && o.indexOf("type=negative") > -1 ? s = l("Q0143") : o.indexOf("/search/?key=") > -1 && o.indexOf("type=copyright") > -1 ? s = l("Q0144") : o.indexOf("/company/network") > -1 ? s = l("Q0145") : o.indexOf("/company/relation") > -1 ? s = l("Q0146") : o.indexOf("/company/") > -1 ? s = l("Q0147") : o.indexOf("/terms") > -1 ? s = l("Q0148") : o.indexOf("/questions") > -1 ? s = l("Q0149") : o.indexOf("/about") > -1 && (s = l("Q0150"))
                } else
                    this.id == "formIndex" && (s = l("Q0007"));
                if (typeof _hmt == "undefined")
                    return;
                _hmt.push(["_trackEvent", s.category, "submit", s.eventDesc])
            }),
            c(),
            $(document).on("click", "#globalFedback", function() {
                $("#feedbackModal").modal("show")
            }),
            $(document).on("click", "#qxb-navbar .nav_link a", function() {
                var e = $(this).attr("href");
                e == "/members" && u.track("进入会员服务页", {
                    "触发位置": "导航栏"
                })
            }),
        {
            getSelectionText: h,
            getUrlParam: p,
            resize: c
        }
    }),
    require(["avalon", "common", "login", "global", "statistics"], function(e, t, n, r, i) {
        $(".login-content input[placeholder]").placeholder(),
            setTimeout(function() {
                var t = $("input[name=account]").val() || ""
                    , r = $("input[name=password]").val() || "";
                e.scan(),
                    n.init({
                        account: t,
                        password: r
                    })
            }, 500),
            $("#myCarousel").carousel({
                interval: 3e3
            })
    }),
    define("javascripts/viewmodels/vm_login_single", function() {});
