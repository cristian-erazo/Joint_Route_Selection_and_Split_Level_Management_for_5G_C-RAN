graph [
  node [
    id 0
    label "0"
    type 2
    prc 2
  ]
  node [
    id 1
    label "1"
    type 2
    prc 4
  ]
  node [
    id 2
    label "2"
    type 1
    prc 5
    ant 4
    prb 5
    x 40
    y 34
  ]
  node [
    id 3
    label "3"
    type 1
    prc 5
    ant 3
    prb 5
    x 27
    y 77
  ]
  node [
    id 4
    label "4"
    type 1
    prc 1
    ant 4
    prb 3
    x 41
    y 69
  ]
  node [
    id 5
    label "5"
    type 1
    prc 4
    ant 6
    prb 4
    x 68
    y 98
  ]
  node [
    id 6
    label "6"
    type 1
    prc 5
    ant 4
    prb 5
    x 60
    y 34
  ]
  edge [
    source 0
    target 2
    bandwith 349
    delay 284
  ]
  edge [
    source 0
    target 4
    bandwith 364
    delay 498
  ]
  edge [
    source 1
    target 6
    bandwith 214
    delay 116
  ]
  edge [
    source 1
    target 5
    bandwith 490
    delay 178
  ]
  edge [
    source 1
    target 3
    bandwith 818
    delay 117
  ]
]
