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
    prc 5
  ]
  node [
    id 2
    label "2"
    type 1
    prc 4
    ant 2
    prb 3
    x 38
    y 112
  ]
  node [
    id 3
    label "3"
    type 1
    prc 3
    ant 5
    prb 5
    x 29
    y 71
  ]
  node [
    id 4
    label "4"
    type 1
    prc 4
    ant 3
    prb 5
    x 112
    y 33
  ]
  edge [
    source 0
    target 2
    bandwith 635
    delay 487
  ]
  edge [
    source 1
    target 4
    bandwith 100
    delay 348
  ]
  edge [
    source 1
    target 3
    bandwith 835
    delay 182
  ]
]
