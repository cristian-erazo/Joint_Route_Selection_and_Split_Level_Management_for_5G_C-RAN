graph [
  node [
    id 0
    label "0"
    type 2
    prc 4
  ]
  node [
    id 1
    label "1"
    type 2
    prc 3
  ]
  node [
    id 2
    label "2"
    type 1
    prc 1
    ant 2
    prb 2
    x 33
    y 111
  ]
  node [
    id 3
    label "3"
    type 1
    prc 5
    ant 10
    prb 3
    x 52
    y 15
  ]
  edge [
    source 0
    target 3
    bandwith 156
    delay 426
  ]
  edge [
    source 1
    target 2
    bandwith 755
    delay 339
  ]
]
