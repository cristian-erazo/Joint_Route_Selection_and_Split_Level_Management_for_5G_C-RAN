graph [
  node [
    id 0
    label "0"
    type 2
    prc 5
  ]
  node [
    id 1
    label "1"
    type 1
    prc 1
    ant 2
    prb 5
    x 21
    y 51
  ]
  node [
    id 2
    label "2"
    type 1
    prc 1
    ant 8
    prb 3
    x 106
    y 77
  ]
  node [
    id 3
    label "3"
    type 1
    prc 4
    ant 9
    prb 6
    x 22
    y 112
  ]
  edge [
    source 0
    target 3
    bandwith 219
    delay 299
  ]
  edge [
    source 0
    target 1
    bandwith 278
    delay 406
  ]
  edge [
    source 0
    target 2
    bandwith 109
    delay 416
  ]
]
