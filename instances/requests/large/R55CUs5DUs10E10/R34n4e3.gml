graph [
  node [
    id 0
    label "0"
    type 2
    prc 3
  ]
  node [
    id 1
    label "1"
    type 1
    prc 2
    ant 3
    prb 3
    x 86
    y 24
  ]
  node [
    id 2
    label "2"
    type 1
    prc 3
    ant 8
    prb 3
    x 42
    y 76
  ]
  node [
    id 3
    label "3"
    type 1
    prc 5
    ant 3
    prb 5
    x 15
    y 110
  ]
  edge [
    source 0
    target 1
    bandwith 587
    delay 153
  ]
  edge [
    source 0
    target 3
    bandwith 624
    delay 192
  ]
  edge [
    source 0
    target 2
    bandwith 619
    delay 332
  ]
]
